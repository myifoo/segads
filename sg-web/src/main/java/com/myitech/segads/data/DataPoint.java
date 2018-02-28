package com.myitech.segads.data;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

import java.util.LinkedList;
import java.util.List;

/**
 * Description:
 * <p>
 * Created by A.T on 2018/02/26
 */
public class DataPoint {
    long time;
    double value;

    public DataPoint(long time, double value) {
        this.time = time;
        this.value = value;
    }


    static public List<DataPoint> toList(JSONArray array) {
        List<DataPoint> result = new LinkedList<>();

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONArray datapoint = array.getJSONArray(i);
                // todo 这里进行 json 转 double 的时候，有可能数据精度会丢失；
                result.add(new DataPoint((datapoint.getLong(0)), datapoint.getDouble(1)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    @Override
    public String toString() {
        return "DataPoint [" + time +", "+ value + "]";
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
