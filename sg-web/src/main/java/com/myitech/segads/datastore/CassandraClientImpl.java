package com.myitech.segads.datastore;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.NoHostAvailableException;
import com.datastax.driver.core.policies.LoadBalancingPolicy;
import com.myitech.segads.Segads;
import com.myitech.segads.core.events.DatabaseUnavailableEvent;
import com.myitech.segads.exceptions.InternalDatastoreException;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by A.T on 2018/1/7.
 */
@Deprecated
public class CassandraClientImpl  implements CassandraClient{

    public final Logger logger = LoggerFactory.getLogger(CassandraClientImpl.class);
    private final Cluster.Builder builder;
    private Cluster m_cluster;

    private Map<String, Session> sessions = new HashMap<>();

    // TODO 是不是放在 postConstruct 中比较妥当呢？
    public CassandraClientImpl() {
        builder = new Cluster.Builder();
        m_cluster = builder.addContactPoint("127.0.0.1").build();

        sessions.put("segads", m_cluster.connect("segads"));
        sessions.put("system_schema", m_cluster.connect("system_schema"));

        Schema.setupSchema(this); // TODO 数据库连接失败的情况怎么处理？
    }

    @Override
    public Session getKeyspaceSession(String m_keyspace) {
        Session session = sessions.get(m_keyspace);

        if (session != null & !session.isClosed()) {
            return session;
        }

        if (m_cluster.isClosed()) {
                m_cluster = builder.build();
        }

        try {
            session = sessions.put(m_keyspace, m_cluster.connect(m_keyspace));
        } catch (Exception e) {
            // 通知对此事件感兴趣的组件，比如某些 Filter，对数据库有需求的请求直接返回 500
            Segads.post(new DatabaseUnavailableEvent("Session is invalid!"));
            // 启动定时器程序，定期轮训的检测数据库状态。

        }

        return session;
    }

    @Override
    public Session getSession() {
        if (m_cluster.isClosed()) {
            m_cluster = builder.build();
        }

        return m_cluster.connect();
    }

    @Override
    public void close() {
        m_cluster.close();
    }

    private void struggle() {
//        // 延迟一分钟重新执行
//        logger.info("Struggle to start WebServer again 1 minute later ... ");
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                logger.info("Start a new Thread.");
//                new Thread(server, NAME).start();
//            }
//        }, DELAY);
    }
}
