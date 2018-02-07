package com.myitech.segads.datastore;

import com.datastax.driver.core.Session;
import org.jvnet.hk2.annotations.Contract;

@Contract
@Deprecated
public interface CassandraClient {
    Session getKeyspaceSession(String keyspace);
    Session getSession();
    void close();
}
