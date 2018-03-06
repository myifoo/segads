package com.myitech.segads.db;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.myitech.segads.Segads;
import com.myitech.segads.exceptions.DatabaseInstallFailedException;

/**
 * Created by A.T on 2018/1/8.
 *
 *      如果涉及到动态的 schema 构建，最好使用 datastax 的 SchemaBuilder 去构建所需的schema；
 *      由于本系统的 schema 是静态的，因此初始化采用简单方式。
 *
 */
public class CassandraSchema implements Schema{
    private final String CREATE_KEYSPACE = "" +
            "CREATE KEYSPACE IF NOT EXISTS %s" +
            "  WITH REPLICATION = {'class': 'SimpleStrategy'," +
            "  'replication_factor' : 1}";

    private final String SESSION = "" +
            "CREATE TABLE IF NOT EXISTS session (name text, category text, " +
            "key text, int_v int, str_v text, set_v set<text>, list_v list<text>, " +
            "map_v map<text, text>, PRIMARY KEY (name, category, key));";

    // 数据存储的类型选择，影响很大，是存储text，还是float？ 后续还要优化重构，还要考虑float转换为json过程中造成的
    // 数据精度丢失问题；
    //
    private final String DATA = "" +
            "CREATE TABLE IF NOT EXISTS data (id text, type text, time bigint, " +
            "value double, properties map<text, text> static, primary key(id, type, time));";

    @Override
    public void initDatabase() throws DatabaseInstallFailedException {
        String host = Segads.getProperty(CassandraDatastore.DATABASE_HOST);
        String keyspace = Segads.getProperty(CassandraDatastore.KEYSPACE_KEY);
        try (
                Cluster cluster = new Cluster.Builder()
                .addContactPoint(host)
                .build()
        ) {
            Session session = cluster.connect();
            session.execute(String.format(CREATE_KEYSPACE, keyspace));
            session.close();

            session = cluster.connect(keyspace);
            session.execute(SESSION);
            session.execute(DATA);
            session.close();
        } catch (Exception e) { // TODO 这里其实主要会是 NoHostAvailableException
            throw new DatabaseInstallFailedException(
                    String.format("Cassandra Schema install failed caused by :%s\n", e.getMessage()), e);
        }
    }
}
