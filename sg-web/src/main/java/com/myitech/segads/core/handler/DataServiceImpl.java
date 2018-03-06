package com.myitech.segads.core.handler;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.myitech.segads.db.Datastore;
import com.myitech.segads.core.DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;

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
    public void insert(Object[][] datapoints, Map<String, String> attributes) {
        StringBuilder cql = new StringBuilder("INSERT INTO data(id, type, time, value) VALUES ('")
                .append(attributes.get("id"))
                .append("', '")
                .append(attributes.get("type"))
                .append("', ?")
                .append(", ?);");

        logger.debug("Execute : {}", cql);

        PreparedStatement statement = datastore.prepare(cql.toString());
        BatchStatement batchStatement = new BatchStatement();

        Arrays.stream(datapoints).forEach(datapoint -> batchStatement.add(statement.bind(datapoint[0], datapoint[1])));
        datastore.execute(batchStatement); // TODO Note: max size limited !!!!!
    }

    @Override
    public Object[][] get(String id) {
        logger.debug("CQl Query : {}", id);
        int count = datastore.count("SELECT count(*) FROM data where id = ? and type='raw';", id);
        Object[][] target = new Object[count][2];

        ResultSet rows = datastore.query("SELECT time, value FROM data where id = ? and type='raw';", id);
        Iterator<Row> iterator = rows.iterator();
        for (int i = 0; iterator.hasNext() ; i++) {
            target[i][0] = iterator.next().getLong("time");
            target[i][1] = iterator.next().getDouble("value");
        }

        return target;
    }

    @Override
    public void delete(String id) {
        String cql = "DELETE FROM data WHERE id = ?;";
        datastore.execute(cql, id);
    }

}
