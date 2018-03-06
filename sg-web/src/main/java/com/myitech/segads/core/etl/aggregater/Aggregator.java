package com.myitech.segads.core.etl.aggregater;

import org.codehaus.jettison.json.JSONArray;

import java.util.Map;

/**
 * Description:
 * <p>
 * Created by A.T on 2018/03/05
 */
public interface Aggregator {
    JSONArray act(Map<String, String> rules);
}
