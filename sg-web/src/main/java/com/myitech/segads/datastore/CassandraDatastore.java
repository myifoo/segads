package com.myitech.segads.datastore;

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

    private static String HOST = "127.0.0.1";
    private static String KEYSPACE = "segads";
    private static int DELAY = 1000*60;

    private CassandraClient client;

    @Override
    public void postConstruct() {
        client = new CassandraClient();
        client.init();
    }

    /**
     * 目前只支持cassandra database，所以将CassandraClient放在这里，结构上简单清晰一些，以后支持多数据库时，再优化吧。
     *
     * Database 提供了 service view 接口，也就是CQl能提供的一些功能接口；
     * CassandraClient 是与 Cassandra 数据库进行交互的实体，service 其实是不关心的，因此将其封装在 Database 里面。
     */
    public class CassandraClient {
        private Cluster.Builder builder;
        private Cluster m_cluster;
        private Session session;
        Timer timer = new Timer("DatabaseTimer");

        private volatile boolean posted;

        // 数据库连接的初始化工作，
        public void init() {
            builder = new Cluster.Builder().addContactPoint(HOST);
            m_cluster = builder.build();
            session = m_cluster.connect(KEYSPACE);
        }

        // 这里直接返回 session，对于任何连接访问失败的异常，由上层的使用者进行管理；当异常发生时，调用
        // client 的 struggle 接口，进行轮询检测。
        public Session getSession() {
            return session;
        }

        // TODO 是否能释放掉所有的资源？
        public void close() {
            m_cluster.close();
        }

        /**
         *  需要注意的是，struggle一旦调用，就会持续下去，每隔一分钟 refresh 一次，直到连接重新建立成功
         */
        public void struggle() {
            logger.warn("Struggle to init Cassandra Client again 1 minute later ... ");

            if (!posted) {
                Segads.post(new InternalDatastoreException(InternalDatastoreException.State.INACTIVE));
                posted = true;
            }

            // 注意这里只会执行一次，如果没有成功，就再调用一次
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    logger.warn("Start a new Thread.");
                    client.refresh();
                }
            }, DELAY);
        }

        private void refresh() {
            try {
                if (m_cluster.isClosed())
                    m_cluster.init();

                session = m_cluster.connect(KEYSPACE);

                Segads.post(new InternalDatastoreException(InternalDatastoreException.State.ACTIVE));
                posted = false;
            } catch (Exception e) {
                struggle();
            }
        }
    }

    public ResultSet execute(String cql, Object... values) {
        try {
            return client.getSession().execute(cql, values);
        } catch (NoHostAvailableException e) { // 这里只捕获 NoHost 异常，其他异常交由上层处理
            client.struggle();
            throw new InternalDatastoreException(e);
        }
    }

    @Override
    public ResultSet execute(Statement statement) {
        try {
            return client.getSession().execute(statement);
        } catch (NoHostAvailableException e) {
            client.struggle();
            throw new InternalDatastoreException(e);
        }
    }

    @Override
    public PreparedStatement prepare(String cql) {
        try {
            return client.getSession().prepare(cql);
        } catch (NoHostAvailableException e) {
            client.struggle();
            throw new InternalDatastoreException(e);
        }
    }

    @Override
    public ResultSet query(String cql, Object... values) {
        return execute(cql, values);
    }

    @Override
    @Deprecated
    public ResultSet getColumnNames(String table) {
        return execute("select column_name from columns where table_name=? allow filtering;", table);
    }
}
