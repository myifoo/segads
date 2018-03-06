package com.myitech.segads.core.etl.aggregater;


import org.codehaus.jettison.json.JSONArray;

import java.util.Map;

/**
 * Description:
 * <p>
 * Created by A.T on 2018/03/05
 */
public class AvgAggregator extends BaseAggregator implements Aggregator{
    @Override
    public JSONArray act(Map<String, String> rules) {
        return null;
    }
    //
//    public JSONArray act(Map<String, String> rules) {
//        return actByWeek();
//    }
//
//    private JSONArray actByWeek() {
//        JSONArray result = new JSONArray();
//        double period = 7*24*3600*1000;
////
////
////        try {
////            JSONArray firstElement = timeSeries.getJSONArray(0);
////            long start = firstElement.getLong(0);
////            double sum = 0;
////
////            for (int i = 0; i < timeSeries.length(); i++) {
////                JSONArray data = timeSeries.getJSONArray(i);
////                long current = data.getLong(0);
////
////                if (current - start > period) {
////                    result.put(new JSONArray().put(start - start%period).put(sum));
////                    start = current;
////                    sum += data.getDouble(1);
////                } else {
////                    sum += data.getDouble(1);
////                }
////            }
////        } catch (JSONException e) {
////            e.printStackTrace();
////        }
//
//
//        return result;
//    }
}
