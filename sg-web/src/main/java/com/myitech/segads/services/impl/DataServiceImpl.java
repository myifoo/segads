package com.myitech.segads.services.impl;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.myitech.segads.core.db.Datastore;
import com.myitech.segads.data.RecordValue;
import com.myitech.segads.services.DataService;
import net.sourceforge.openforecast.DataPoint;
import net.sourceforge.openforecast.DataSet;
import net.sourceforge.openforecast.Observation;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Description:
 * <p>
 * Created by A.T on 2018/02/26
 */
public class DataServiceImpl implements DataService {
    public static final Logger logger = LoggerFactory.getLogger(DataServiceImpl.class);

    @Inject
    Datastore datastore;

    @Override
    public void insert(List<RecordValue> timeseries, String id, TYPE type) {
        StringBuilder cql = new StringBuilder("INSERT INTO data(id, type, time, value) VALUES ('")
                .append(id) // 在cql中，字符串变量应该使用单引号
                .append("', '")
                .append(type)
                .append("', ?")
                .append(", ?);");

        PreparedStatement statement = datastore.prepare(cql.toString());
        BatchStatement batchStatement = new BatchStatement();
        timeseries.forEach(recordValue -> batchStatement.add(statement.bind(recordValue.getTime(), recordValue.getValue())));

        datastore.execute(batchStatement); // Note: max size limited !!!!!
    }

    @Override
    public JSONArray get(String id) {
        JSONArray array = new JSONArray();
        queryById((row)-> {
                try {
                    array.put(new JSONArray().put(row.getLong("time"))
                            .put(row.getDouble("value")));
                } catch (JSONException e) {
                    logger.error("Error : {}", e.getMessage());
                }
        }, id);
        return array;
    }

    @Override
    public List<DataPoint> getList(String id) {
        List<DataPoint> array = new ArrayList<>();

        queryById((row)-> {
                    DataPoint dp = new Observation(row.getDouble("value"));
                    dp.setIndependentValue("time", row.getLong("time"));
                    array.add(dp);
                }, id);
        return array;
    }

    @Override
    public void delete(String id) {
        String cql = "DELETE FROM data WHERE id = ?;";
        datastore.execute(cql, id);
    }

    @Override
    public DataSet getDataSet(String id) {
        DataSet ds = new DataSet();
        queryById((row)-> {
            DataPoint dp = new Observation(row.getDouble("value"));
            dp.setIndependentValue("time", row.getLong("time"));
            ds.add(dp);
        }, id);

        return ds;
    }

    private void queryById(Consumer<? super Row> callback, String id) {
        try {
            ResultSet rows = datastore.query("SELECT time, value FROM data where id = ? and type='raw';", id);
            rows.iterator().forEachRemaining(callback);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
