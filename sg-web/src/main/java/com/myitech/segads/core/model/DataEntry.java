package com.myitech.segads.core.model;

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
public class DataEntry implements Timeable{
    long time;
    double value;
    //            public long logicalIndex = 0; // 逻辑序号，当不考虑业务内涵，作为纯数据处理时，使用该序号

    public DataEntry(long time, double value) {
        this.time = time;
        this.value = value;
    }


    static public List<DataEntry> toList(JSONArray array) {
        List<DataEntry> result = new LinkedList<>();

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONArray jsonArray = array.getJSONArray(i);
                // todo 这里进行 json 转 double 的时候，有可能数据精度会丢失；
                result.add(new DataEntry((jsonArray.getLong(0)), jsonArray.getDouble(1)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    @Override
    public String toString() {
        return "DataEntry [" + time +", "+ value + "]";
    }

    public boolean equals(Object other_obj) {
        if (!(other_obj instanceof DataEntry)) {
            return false;
        }
        DataEntry other = (DataEntry) other_obj;
        if (time != other.time) {
            return false;
        }
        if (value != other.value) {
            return false;
        }
//        if (logicalIndex != other.logicalIndex) {
//            return false;
//        }
        return true;
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
