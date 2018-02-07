package com.myitech.segads.datastore;

/**
 * Created by A.T on 2018/1/7.
 */
@Deprecated
public class CassandraConfiguration {
    public static final String READ_CONSISTENCY_LEVEL = "segads.datastore.cassandra.read_consistency_level";
    public static final String WRITE_CONSISTENCY_LEVEL = "segads.datastore.cassandra.write_consistency_level";
    public static final String DATAPOINT_TTL = "segads.datastore.cassandra.datapoint_ttl";

    public static final String ROW_KEY_CACHE_SIZE_PROPERTY = "segads.datastore.cassandra.row_key_cache_size";
    public static final String STRING_CACHE_SIZE_PROPERTY = "segads.datastore.cassandra.string_cache_size";

    public static final String KEYSPACE_PROPERTY = "segads.datastore.cassandra.keyspace";
    public static final String HOST_LIST_PROPERTY = "segads.datastore.cassandra.cql_host_list";
    public static final String SIMULTANIOUS_QUERIES = "segads.datastore.cassandra.simultaneous_cql_queries";
    public static final String QUERY_LIMIT = "segads.datastore.cassandra.query_limit";
    public static final String QUERY_READER_THREADS = "segads.datastore.cassandra.query_reader_threads";

    public static final String AUTH_USER_NAME = "segads.datastore.cassandra.auth.user_name";
    public static final String AUTH_PASSWORD = "segads.datastore.cassandra.auth.password";
    public static final String USE_SSL = "segads.datastore.cassandra.use_ssl";

    public static final String LOCAL_CORE_CONNECTIONS = "segads.datastore.cassandra.connections_per_host.local.core";
    public static final String LOCAL_MAX_CONNECTIONS = "segads.datastore.cassandra.connections_per_host.local.max";

    public static final String REMOTE_CORE_CONNECTIONS = "segads.datastore.cassandra.connections_per_host.remote.core";
    public static final String REMOTE_MAX_CONNECTIONS = "segads.datastore.cassandra.connections_per_host.remote.max";

    public static final String LOCAL_MAX_REQ_PER_CONN = "segads.datastore.cassandra.max_requests_per_connection.local";
    public static final String REMOTE_MAX_REQ_PER_CONN = "segads.datastore.cassandra.max_requests_per_connection.remote";

    public static final String MAX_QUEUE_SIZE = "segads.datastore.cassandra.max_queue_size";

    public static final String LOCAL_DATACENTER = "segads.datastore.cassandra.local_datacenter";
}
