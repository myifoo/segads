package com.myitech.segads.services.impl;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.myitech.segads.core.db.Datastore;
import com.myitech.segads.data.DataPoint;
import com.myitech.segads.services.DatapointsService;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

/**
 * Description:
 * <p>
 * Created by A.T on 2018/02/26
 */
public class DatapointsServiceImpl implements DatapointsService{
    public static final Logger logger = LoggerFactory.getLogger(DatapointsServiceImpl.class);

    @Inject
    Datastore datastore;

    @Override
    public void insert(List<DataPoint> timeseries, String id, TYPE type) {
        StringBuilder cql = new StringBuilder("INSERT INTO data(id, type, time, value) VALUES ('")
                .append(id) // 在cql中，字符串变量应该使用单引号
                .append("', '")
                .append(type)
                .append("', ?")
                .append(", ?);");

        PreparedStatement statement = datastore.prepare(cql.toString());
        BatchStatement batchStatement = new BatchStatement();
        timeseries.forEach(dataPoint -> batchStatement.add(statement.bind(dataPoint.getTime(), dataPoint.getValue())));

        datastore.execute(batchStatement); // Note: max size limited !!!!!
    }

    @Override
    public JSONArray get(String id) {
        JSONArray array = new JSONArray();

        try {
            ResultSet rows = datastore.query("SELECT time, value FROM data where id = ? and type='raw';", id);
            rows.iterator().forEachRemaining((row)-> {
                try {
                    array.put(new JSONArray().put(row.getLong("time"))
                                           .put(row.getDouble("value")));
                } catch (JSONException e) {
                    logger.error("Error : {}", e.getMessage());
                }
            });
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return array;
    }
}
