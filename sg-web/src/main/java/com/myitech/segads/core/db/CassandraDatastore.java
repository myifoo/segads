package com.myitech.segads.core.db;

import com.datastax.driver.core.*;
import com.datastax.driver.core.exceptions.NoHostAvailableException;
import com.myitech.segads.Segads;
import com.myitech.segads.exceptions.InternalDatastoreException;
import org.glassfish.hk2.api.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public class CassandraDatastore implements Datastore, PostConstruct {
    public static final Logger logger = LoggerFactory.getLogger(CassandraDatastore.class);
    public static String KEYSPACE_KEY = "segads.datastore.cassandra.keyspace";
    public static String DATABASE_HOST = "segads.datastore.cassandra.cql_host_list";

    private static String HOST = "127.0.0.1"; // default value
    private static String KEYSPACE = "segads"; // default value
    private static int DELAY = 1000*60;

    private CassandraClient client;

    @Override
    public void postConstruct() {
        client = new CassandraClient();
        HOST = Segads.getProperty(DATABASE_HOST);
        KEYSPACE = Segads.getProperty(KEYSPACE_KEY);

        client.init();
    }

    public ResultSet execute(String cql, Object... values) {
        try {
            return client.getSession().execute(cql, values);
        } catch (NoHostAvailableException | NullPointerException e) { // 其他异常交由上层处理
            client.struggle();
            throw new InternalDatastoreException(e);
        }
    }

    @Override
    public ResultSet execute(Statement statement) {
        try {
            return client.getSession().execute(statement);
        } catch (NoHostAvailableException | NullPointerException e) {
            client.struggle();
            throw new InternalDatastoreException(e);
        }
    }

    @Override
    public PreparedStatement prepare(String cql) {
        try {
            return client.getSession().prepare(cql);
        } catch (NoHostAvailableException | NullPointerException e) {
            client.struggle();
            throw new InternalDatastoreException(e);
        }
    }

    @Override
    public ResultSet query(String cql, Object... values) {
        return execute(cql, values);
    }

    /**
     * 目前只支持cassandra database，所以将CassandraClient放在这里，结构上简单清晰一些，以后支持多数据库时，再优化吧。
     *
     * Database 提供了 service view 接口，也就是CQl能提供的一些功能接口；
     * CassandraClient 是与 Cassandra 数据库进行交互的实体，service 其实是不关心的，因此将其封装在 Database 里面。
     */
    public class CassandraClient {
        private Cluster.Builder builder;
        private Cluster cluster;
        private Session session;// The Session is what you use to execute queries. Likewise, it is thread-safe and should be reused.
        private Timer timer;
        private volatile boolean struggled = false;

        // 数据库连接的初始化工作，
        public void init() {
            builder = new Cluster.Builder().addContactPoint(HOST);
            cluster = builder.build();

            try {
                session = cluster.connect(KEYSPACE);
            } catch (Exception e) {
                logger.error("Init CassandraClient failed!");
            }
        }

        // 这里直接返回 session，对于任何连接访问失败的异常，由上层的使用者进行管理；
        public Session getSession() {
            return session;
        }

        // Close the cluster after we’re done with it. This will also close any session that was created from this
        // cluster.
        // This step is important because it frees underlying resources (TCP connections, thread pools...). In a
        // real application, you would typically do this at shutdown (for example, when undeploying your webapp).
        public void close() {
            cluster.close();
        }

        // 需要注意的是，struggle一旦调用，就会持续下去，每隔一分钟 refresh 一次，直到连接重新建立成功
        public void struggle() {
            if (struggled)
                return; // 如果struggle已经被调用，当再次调用时，则直接返回，

            logger.warn("Struggle to init Cassandra Client again 1 minute later ... ");

            Segads.post(new InternalDatastoreException(InternalDatastoreException.State.INACTIVE));
            timer = new Timer("DatabaseTimer");

            // invoke refresh() every 1 minutes
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    client.refresh();
                }
            }, DELAY, DELAY);
        }

        private void refresh() {
            try {
                logger.warn("Invoke refresh() ..... ");

                if (cluster.isClosed())
                    cluster.init();

                session = cluster.connect(KEYSPACE);
            } catch (Exception e) {
                // refresh failed. struggle again
                logger.error(e.getMessage());
                return; //
            }

            // refresh success, post ACTIVE event and set posted to false
            Segads.post(new InternalDatastoreException(InternalDatastoreException.State.ACTIVE));
            timer.cancel();
            timer = null;
            struggled = false;
        }
    }
}
