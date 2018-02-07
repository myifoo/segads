package com.myitech.segads.core;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.myitech.segads.Segads;
import com.myitech.segads.binders.ApplicationBinder;
import com.myitech.segads.core.events.LifeCycleEvent;
import com.myitech.segads.datastore.Schema;
import com.myitech.segads.exceptions.LifecycleException;
import com.myitech.segads.utils.SegadsProperties;
import org.apache.commons.lang.StringUtils;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.hk2.api.PostConstruct;
import org.glassfish.hk2.api.messaging.SubscribeTo;
import org.glassfish.hk2.api.messaging.Topic;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.jettison.JettisonFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.MvcFeature;
import org.glassfish.jersey.server.mvc.freemarker.FreemarkerMvcFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.net.URI;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Description:
 * <p>
 * Created by A.T on 2018/02/01
 */
public class WebServer implements LifeCycle, Runnable, Server, PostConstruct{
    private static final String NAME = "WebServer";
    private static WebServer server = null;
    private static String BASE_URI = "http://localhost:80/segads/"; // Base URI the Grizzly HTTP server will listen on
    private static String PACKAGES_SCAN = "com.myitech.segads";
    private static String FREEMARKER_BASE = "freemarker";
    private static String HTML_BASE = "/html";
    private static State state = State.INACTIVE;

    private Logger logger = LoggerFactory.getLogger(WebServer.class);
    private HttpServer httpServer;
    private Status status = Status.WAITING;
    private int DELAY = 60*1000;
    private Timer timer;

    private Properties properties;

    // 该方法不会被频繁调用，暂时不用同步处理
    public static WebServer newInstance(Properties properties) {
        if (server == null ) server = new WebServer(properties);

        return server;
    }

    private WebServer(Properties properties) {
        this.properties = properties;
        this.timer = new Timer("WebTimer");
    }

    @Override
    public void init() throws LifecycleException {
        // step 1: parse configuration
        logger.info("Start to parse and config Web Server ...");
        configParse();
        // step 2: check database state
        logger.info("Start to check the status of database ...");

        // TODO 后续优化代码结构
        String database = properties.getProperty(SegadsProperties.DATABASE);
        String host = properties.getProperty(SegadsProperties.DATABASE_HOST);
        if (StringUtils.equals(database, "cassandra")) {
            try (
                    Cluster cluster = new Cluster.Builder()
                    .addContactPoint(host)
                    .build()
            ) {
                Session session = cluster.connect("system_schema");
                Schema.setupSchema(session); // TODO 考虑考虑放在这里是否合适
                session.close();
            } catch (Exception e) {
                logger.error("Failed when check cassandra database status!");
                throw new LifecycleException(e);
            }
        }

        // 注册为EventBus的订阅者
        Segads.register(this);
    }

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     */
    @Override
    public void start() throws LifecycleException {
        logger.info("Start http server ... ");

        try {
            // create a resource config that scans for JAX-RS resources and providers
            final ResourceConfig rc = new ResourceConfig()
                    .packages(PACKAGES_SCAN) // packages path for resources loading
                    .property(MvcFeature.TEMPLATE_BASE_PATH, FREEMARKER_BASE) // config freemarker view files's base path
                    .register(LoggingFeature.class)
                    .register(FreemarkerMvcFeature.class)
                    .register(JettisonFeature.class)
                    .registerInstances(new ApplicationBinder()); //

            // create and start a new instance of grizzly http server
            // exposing the Jersey application at BASE_URI
            httpServer = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);

            // Set StaticHttpHandler to handle http server's static resources
            String htmlPath = this.getClass().getResource(HTML_BASE).getPath(); // TODO, 部署后要根据实际目录修正！classes 同级目录下的 html 目录
            HttpHandler handler = new StaticHttpHandler(htmlPath);
            httpServer.getServerConfiguration().addHttpHandler(handler, "/");

//            InjectionManagerProvider.get

            logger.info("Jersey app started with WADL available at {} application.wadl\n ", BASE_URI);

        } catch (Exception e) {
            throw new LifecycleException(e); // just convert to self defined exception
        }
    }

    @Override
    public void stop() {
        logger.info("Stop WebServer ... ");
        httpServer.shutdown();
        state = State.INACTIVE;
    }

    @Override
    public void run() {
        logger.info("Run WebServer ： state - [{}]， status - [{}]", state, status);

        // avoid to start multiple threads concurrently
        synchronized (this) {
            if (state.equals(State.ACTIVE)) // only one server in active state
                return;

            try {
                modStatus(Status.WAITING);
                init();
                modStatus(Status.INIT);
                start();
                logger.info("\n----------------- Segads WebServer started -----------------\n");

                state = State.ACTIVE;
                modStatus(Status.RUNNING);

                timer.cancel();
            } catch (LifecycleException e) {
                logger.error("Start WebServer failed ! \n{}", e.getMessage());
                modStatus(Status.FAILED);
                state = State.STRUGGLING;
                struggle();
            }
        }
    }

    private void configParse() {
        if (properties.getProperty(SegadsProperties.BASE_URI) != null) {
            BASE_URI = properties.getProperty(SegadsProperties.BASE_URI);
        }

        if (properties.getProperty(SegadsProperties.FREEMARKER_BASE) !=  null)
            FREEMARKER_BASE = properties.getProperty(SegadsProperties.FREEMARKER_BASE);

        if (properties.getProperty(SegadsProperties.HTML_BASE) != null)
            HTML_BASE = properties.getProperty(SegadsProperties.HTML_BASE);
    }

    public Status getStatus() {
        return status;
    }

    /**
     *  WebServer 是核心线程，当启动失败后，会延迟1分钟再次执行；
     */
    private void struggle() {
        // 延迟一分钟重新执行
        logger.warn("Struggle to start WebServer again 1 minute later ... ");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                logger.warn("Start a new Thread.");
                new Thread(server, NAME).start();
            }
        }, DELAY);
    }

    private void modStatus(Status status) {
        this.status = status;
    }

    @Override
    public void postConstruct() {
        System.out.println("PostConstruct WebServer ............. ");
    }

    @Subscribe
    public void onDatabaseUnavailableEvent(LifeCycleEvent event) {
//        httpServer.shutdownNow();
        logger.info("........................... Receive LifeCycleEvent ...............{}.......................... ", event.getMessage());
    }



}
