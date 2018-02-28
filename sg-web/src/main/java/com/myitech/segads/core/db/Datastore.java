package com.myitech.segads.core.db;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Statement;

/**
 * Created by A.T on 2018/1/9.
 */
public interface Datastore {
    ResultSet execute(String cql,  Object... values);
    ResultSet execute(Statement statement);
    PreparedStatement prepare(String cql);

    /**
     *
     * @param cql
     * @param values
     * @return
     */
    ResultSet query(String cql, Object... values);
}
