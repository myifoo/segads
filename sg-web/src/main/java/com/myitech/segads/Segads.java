package com.myitech.segads;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.common.eventbus.EventBus;
import com.myitech.segads.core.WebServer;
import com.myitech.segads.core.db.CassandraSchema;
import com.myitech.segads.exceptions.DatabaseInstallFailedException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * Segads class.
 *
 *      1. 事件处理： EventBus
 *      2. 属性管理： Properties
 *
 */
public class Segads {
    private static final Arguments arguments = new Arguments();
    private static Properties properties;

    private final static CountDownLatch s_shutdownObject = new CountDownLatch(1);

    /**
     *  使用guava的EventBus来处理事件的发布-订阅；HK2也有自己的Event模块，但是由于jersey-hk2对hk2封装时，并没有提供event特性，
     *  导致要使用HK2的Topic来发布-订阅事件变的很曲折，主要是因为HK2的Event模块是基于其DI容器来实现的。guava的EventBus很独立，
     *  因此用起来更方便，没有任何耦合的配置与隐式约定，可读性很好。
     */
    private final static EventBus eventBus = new EventBus();

    static public void register(Object object) {
        eventBus.register(object);
    }

    static public void post(Object event) {
        eventBus.post(event);
    }

    static public String getProperty(String key) {
        return (String)properties.get(key);
    }

    static public void setProperties(Properties source) {
        properties = source;
    }

    public static void main(String[] args) throws IOException {
        Logger logger = LoggerFactory.getLogger(Segads.class);
        String PROPERTIES_FILE = "/conf/segads.properties";

        JCommander commander = new JCommander(arguments);
        try {
            commander.parse(args);
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
            commander.usage();
            System.exit(0);
        }

        if (arguments.helpMessage || arguments.help || arguments.operationCommand == null) {
            commander.usage();
            System.exit(0);
        }

        if (!StringUtils.isEmpty(arguments.propertiesFile)) {
            PROPERTIES_FILE = arguments.propertiesFile;
        }


        // Step 1: Parse static global properties
        Properties properties = new Properties();
        String File = Segads.class.getResource(PROPERTIES_FILE).getFile(); // TODO 部署后要按照部署的路径加载

        try (FileInputStream in = new FileInputStream(File)) {
            properties.load(in);
        }

        Segads.setProperties(properties);

        if (arguments.operationCommand.equals("start")) {
            try {
                final WebServer webServer = WebServer.getInstance();
                Thread webThread= new Thread(webServer,"WebServer");

                Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                    public void run() {
                        webServer.stop();
                        s_shutdownObject.countDown();
                    }
                }));

                webThread.start();
                webThread.join();

                logger.info("........................... Segads started ...........................");

                waitForShutdown();
            } catch (Exception e) {
                logger.error("Failed starting up services", e);
                System.exit(0);
            } finally {
                logger.info("........................... Segads service is now down!...........................");
            }
        } else if (arguments.operationCommand.equals("initdb")) {
            // TODO 有两个问题： 1 数据库问题是否必须系统异常并退出？ 2 这里应该有DatabaseFactory来处理，目前只支持Cassandra，就简单处理了。
            try {
                new CassandraSchema().initDatabase();
                logger.info("........................... Segads initdb over ...........................");
            } catch (DatabaseInstallFailedException e) {
                logger.error("Init database fail！ Caused by {}", e.getMessage());
                System.exit(-1); // 目前暂时简单处理，数据库连接断开不影响系统运行，但是数据库初始化异常则影响。
            }
        }
    }

    private static void waitForShutdown() {
        try {
            s_shutdownObject.await();
        } catch (InterruptedException ignore) {
            Thread.currentThread().interrupt();
        }
    }

    private static class Arguments
    {
        @Parameter(names = "-p", description = "A custom properties file")
        private String propertiesFile;

        @Parameter(names = "-f", description = "File to save export to or read from depending on command.")
        private String exportFile;

        @Parameter(names = "--help", description = "Segads Help message.", help = true)
        private boolean helpMessage;

        @Parameter(names = "-h", description = "Help message.", help = true)
        private boolean help;

        @Parameter(names = "-c", description = "Command to run: export, import, run, start, initdb.")
        private String operationCommand;

    }
}

