package com.myitech.segads.core;

import com.google.common.eventbus.Subscribe;
import com.myitech.segads.Segads;
import com.myitech.segads.core.binders.ApplicationBinder;
import com.myitech.segads.core.events.LifeCycleEvent;
import com.myitech.segads.exceptions.LifecycleException;
import com.myitech.segads.utils.SegadsProperties;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.hk2.api.PostConstruct;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jettison.JettisonFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.MvcFeature;
import org.glassfish.jersey.server.mvc.freemarker.FreemarkerMvcFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Description:
 * <p>
 * Created by A.T on 2018/02/01
 */
public class WebServer implements LifeCycle, Runnable, Server, PostConstruct{
    private static final String NAME = "WebServer";
    /* Singleton */
    private static WebServer server = new WebServer();
    /* Base URI the Grizzly HTTP server will listen on */
    private static String BASE_URI = "http://localhost:80/segads/";
    private static String PACKAGES_SCAN = "com.myitech.segads";
    private static String FREEMARKER_BASE = "freemarker";
    private static String HTML_BASE = "/html";
    private static State state = State.INACTIVE;

    private Logger logger = LoggerFactory.getLogger(WebServer.class);
    private HttpServer httpServer;
    private Status status = Status.WAITING;
    private int DELAY = 60*1000;
    private Timer timer;

    // 该方法不会被频繁调用，暂时不用同步处理
    public static WebServer getInstance() {
        return server;
    }

    private WebServer() {
    }

    @Override
    public void init() throws LifecycleException {
        // step 1: parse configuration
        logger.info("Start to parse and config Web Server ...");
        configParse();

        // TODO web server 是否应该关心 database 的状态？ 当 database 连接断开后，相关接口返回 HTTP-Internal Server Error 是不是更合理一些
        // step 2: check database state

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
                    .packages("org.glassfish.jersey.examples.multipart")
                    .register(MultiPartFeature.class)
                    .registerInstances(new ApplicationBinder()); //

            // create and start a new instance of grizzly http server
            // exposing the Jersey application at BASE_URI
            httpServer = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);

            // Set StaticHttpHandler to handle http server's static resources
            String htmlPath = this.getClass().getResource(HTML_BASE).getPath(); // TODO, 部署后要根据实际目录修正！classes 同级目录下的 html 目录
            HttpHandler handler = new StaticHttpHandler(htmlPath);
            httpServer.getServerConfiguration().addHttpHandler(handler, "/");

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
                init();
                start();

                if (state.equals(State.STRUGGLING)) timer.cancel();
                state = State.ACTIVE;
                logger.info("........................... Segads WebServer started ...........................");
            } catch (LifecycleException e) {
                logger.error("Start WebServer failed ! {}", e.getMessage());
                struggle();
            }
        }
    }

    private void configParse() {
        if (Segads.getProperty(SegadsProperties.BASE_URI) != null) {
            BASE_URI = Segads.getProperty(SegadsProperties.BASE_URI);
        }

        if (Segads.getProperty(SegadsProperties.FREEMARKER_BASE) !=  null)
            FREEMARKER_BASE = Segads.getProperty(SegadsProperties.FREEMARKER_BASE);

        if (Segads.getProperty(SegadsProperties.HTML_BASE) != null)
            HTML_BASE = Segads.getProperty(SegadsProperties.HTML_BASE);
    }

    public Status getStatus() {
        return status;
    }

    private void setStatus(Status status) {
        this.status = status;
    }

    /* WebServer 是核心线程，当启动失败后，会延迟1分钟再次执行 */
    private void struggle() {
        if (state == State.STRUGGLING)
            return; // 避免重复调用

        timer = new Timer("WebTimer");
        // 延迟一分钟重新执行
        logger.warn("Struggle to start WebServer every 1 minute.");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                new Thread(server, NAME).start();
            }
        }, DELAY, DELAY);

        state = State.STRUGGLING;
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
