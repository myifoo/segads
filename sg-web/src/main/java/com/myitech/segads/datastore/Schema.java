package com.myitech.segads.datastore;

import com.datastax.driver.core.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * Created by A.T on 2018/1/8.
 */
public class Schema {
    public static final Logger logger = LoggerFactory.getLogger(Schema.class);


    public static final String KEYSPACE = "segads";

    public static final String CREATE_KEYSPACE = "" +
            "CREATE KEYSPACE IF NOT EXISTS %s" +
            "  WITH REPLICATION = {'class': 'SimpleStrategy'," +
            "  'replication_factor' : 1}";

    public static final String SESSION = "" +
            "CREATE TABLE IF NOT EXISTS session (id text, type text, " +
            "period int static, step int static, primary key(id, type));";

    public static final String TIMESERIES = "" +
            "CREATE TABLE IF NOT EXISTS timeseries (sid text, type text, timestamp int, " +
            "value double, primary key(sid, type, timestamp)) WITH COMPACT STORAGE;";

    public static final String SCHEMA = "" +
            "CREATE TABLE IF NOT EXISTS session_schema (type text, property text, " +
            "primary key(type));";


    private static boolean setupFlag = false;

    // TODO setup schema in INSTALL stage for release version.
    public static void setupSchema(CassandraClient cassandraClient)
    {
        if(setupFlag)
            return;

        logger.info("Start setup Segads Database schema.");

        cassandraClient.getSession().execute(String.format(CREATE_KEYSPACE, KEYSPACE));

        Session session = cassandraClient.getKeyspaceSession(KEYSPACE);
        session.execute(SESSION);
        session.execute(TIMESERIES);
        session.execute(SCHEMA);
        setupFlag = true;
    }

    public static void setupSchema(Session session) {

    }

    public static boolean isSetup() {
        return setupFlag;
    }
}
