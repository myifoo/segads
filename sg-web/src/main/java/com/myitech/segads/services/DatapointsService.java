package com.myitech.segads.services;

import com.myitech.segads.data.DataPoint;
import org.codehaus.jettison.json.JSONArray;

import java.util.List;

/**
 * Description:
 * <p>
 * Created by A.T on 2018/02/26
 */
public interface DatapointsService {
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
    void insert(List<DataPoint> timeseries, String id, TYPE type);

    /**
     *  get all data points by id
     *
     * @param id table record id
     * @return JSONObject
     */
    JSONArray get(String id);
}
