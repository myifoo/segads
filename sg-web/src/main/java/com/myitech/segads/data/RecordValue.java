package com.myitech.segads.data;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

import java.util.LinkedList;
import java.util.List;

/**
 * Description:
 *
 *      对应于一条 data 表中的记录（time, value）
 *
 * <p>
 * Created by A.T on 2018/02/26
 */
public class RecordValue {
    long time;
    double value;

    public RecordValue(long time, double value) {
        this.time = time;
        this.value = value;
    }


    static public List<RecordValue> toList(JSONArray array) {
        List<RecordValue> result = new LinkedList<>();

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONArray jsonArray = array.getJSONArray(i);
                // todo 这里进行 json 转 double 的时候，有可能数据精度会丢失；
                result.add(new RecordValue((jsonArray.getLong(0)), jsonArray.getDouble(1)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    @Override
    public String toString() {
        return "RecordValue [" + time +", "+ value + "]";
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
