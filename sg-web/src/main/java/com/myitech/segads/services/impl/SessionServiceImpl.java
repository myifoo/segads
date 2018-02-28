package com.myitech.segads.services.impl;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.myitech.segads.core.db.Datastore;
import com.myitech.segads.services.SessionService;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;

/**
 * Created by A.T on 2018/1/17.
 */
public class SessionServiceImpl implements SessionService {
    private static final Logger logger = LoggerFactory.getLogger(SessionService.class);

    private static final String INIT_SESSION = "INSERT INTO session (id, type) values (?, 'general');";
    private static final String QUERY_SESSION = "SELECT DISTINCT id from session;";

    PreparedStatement sessionInitStatement;
    PreparedStatement querySessionStatement;

    Datastore datastore;

    @Inject
    public SessionServiceImpl(Datastore datastore) {
        logger.info("Prepare frequently-used query statements.");
        this.datastore = datastore;
        sessionInitStatement = datastore.prepare(INIT_SESSION);
        querySessionStatement = datastore.prepare(QUERY_SESSION);
    }

    @Override
    public void init(String key) {
        datastore.execute(sessionInitStatement.bind(key));
    }

    @Override
    public List<String> list() {
        ResultSet rows = datastore.query("SELECT DISTINCT id from session;");
        List<String> list = new ArrayList<>();

        for (Row row : rows) {
            list.add(row.getString("id"));
        }
        return list;
    }

    @Override
    public void destroy(String key) {
        datastore.execute("DELETE FROM session where id = ?;", key);
        datastore.execute("DELETE FROM timeseries where sid = ?;", key);
    }

    @Override
    public JSONObject describe(String key) {
        try {
            JSONArray jsonArray = new JSONArray();

            ResultSet rows = datastore.query("SELECT json * FROM session where id = ?;", key);
            for (Row one : rows) {
                JSONObject object = new JSONObject(one.getString("[json]"));
                JSONObject tmp = new JSONObject();
                for (Iterator<String> it = object.keys(); it.hasNext(); ) {
                    String column = it.next();
                    if (!object.isNull(column) & !column.equals("id")) {
                        tmp.put(column, object.get(column));
                    }
                }
                jsonArray.put(tmp);
            }

            return new JSONObject().put(key, jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void property(String key, String type, Map<String, String> properties) {
//        // TODO handle exception later
//        // Step 1: check column names
//        ResultSet rows = db.getColumnNames("session");
//        Set<String> columns = new HashSet<>();
//
//        for (Row row : rows) {
//            columns.add(row.getString("column_name"));
//        }
//
//        List<String> list = new ArrayList<>();
//
//        for (String property : properties.keySet()) {
//            if (!columns.contains(property)) {
//                list.add(property);
//            }
//        }
//
//        // Step 2: atler tables
//        if (!list.isEmpty()) {
//            for (String column : list) {
//                db.execute("ALTER TABLE session ADD "+column+" text;");
//            }
//        }
//
//        // Step 3: insert data
//        StringBuilder cql = new StringBuilder("INSERT INTO session ");
//        StringBuilder names = new StringBuilder("(id, type");
//        StringBuilder values = new StringBuilder("('"+key+"', '"+type+"'");
//        for (Map.Entry entry : properties.entrySet()) {
//            names.append(", ").append(entry.getKey()).append(" ");
//            values.append(", '").append(entry.getValue()).append("'");
//        }
//
//        names.append(") ");
//        values.append(") ");
//
//        cql.append(names).append(" values ").append(values).append(";");
//
//        db.execute(cql.toString());
    }

    @Override
    public String show() {
        return null;
    }
}
