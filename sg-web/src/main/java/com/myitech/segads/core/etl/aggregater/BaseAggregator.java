package com.myitech.segads.core.etl.aggregater;

import org.codehaus.jettison.json.JSONArray;

/**
 * Description:
 * <p>
 * Created by A.T on 2018/03/05
 */
public class BaseAggregator {
    protected JSONArray timeSeries;

    public BaseAggregator() {
    }

    public BaseAggregator(JSONArray timeSeries) {
        this.timeSeries = timeSeries;
    }
}
