package com.myitech.segads.services;

import com.myitech.segads.data.RecordValue;
import net.sourceforge.openforecast.DataPoint;
import net.sourceforge.openforecast.DataSet;
import org.codehaus.jettison.json.JSONArray;

import java.util.List;

/**
 * Description:
 * <p>
 * Created by A.T on 2018/02/26
 */
public interface DataService {
    enum TYPE {
        RAW("raw"),
        TEMP("temp");

        private String name;
        TYPE(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }


    /**
     * insert csv data points into database
     *
     * @param timeseries time-series data points
     * @param id record id in cassandra
     * @param type data points type : raw, ...
     */
    void insert(List<RecordValue> timeseries, String id, TYPE type);

    /**
     *  get all data points by id
     *
     * @param id table record id
     * @return JSONObject
     */
    JSONArray get(String id);

    /**
     *  get all data points by id
     *
     * @param id table record id
     * @return List
     */
    List<DataPoint> getList(String id);

    /**
     *
     * @param id
     * @return
     */
    DataSet getDataSet(String id);

    void delete(String id);
}
