package com.myitech.segads.core.model;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.ArrayList;

/**
 * Description:
 * <p>
 * Created by A.T on 2018/03/05
 */
public class TimeSeries implements Jsonable {

    /**
     * DataSequence ： 数据序列，
     */
    public static class DataSequence extends ArrayList<DataEntry> {
        public DataSequence() {
                super();
            }

        public DataSequence initSize(int capacity) {
            this.ensureCapacity(capacity);
            return this;
        }

        public DataSequence append(double value) {
            this.add(new DataEntry(0, value));
            return this;
        }

        public DataSequence append(double[] values) {
            for (int i = 0; i < values.length; ++i) {
                this.add(new DataEntry(i, values[i]));
            }

            return this;
        }

        public DataSequence append(long time, double value) {
            this.add(new DataEntry(time, value));
            return this;
        }

        public DataSequence append(long from, long to, long period) {
            if (to < from) {
                throw new IllegalArgumentException("The start time should be before the end time.");
            }

            for (long i = from; i <= to; i += period) {
                this.add(new DataEntry(i, 0));
            }
            return this;
        }

        public DataSequence append(long[] times, double[] values) {
            if (times.length != values.length) {
                throw new IllegalArgumentException("Length mismatch!");
            }

            for (int i = 0; i < values.length; ++i) {
                if (i > 0 && times[i] < times[i - 1]) {
                    throw new IllegalArgumentException("time=" + times[i] + " at index=" + i + " out of order");
                }
                this.add(new DataEntry(times[i], values[i]));
            }

            return this;
        }


        // 如果没有按时间顺序添加 entry 时，存储序号 != 逻辑序号；
//        public void setLogicalIndices(long firstTimeStamp, long period) {
//            for (DataEntry entry : this) {
//                entry.logicalIndex = (entry.time - firstTimeStamp) / period;
//            }
//        }
//
//        public void setLogicalIndices() {
//            for (int i = 0; i < this.size(); i++) {
//                Entry e = this.get(i);
//                e.logicalIndex = i;
//            }
//        }

        public double[] getValues() {
            double[] fArray = new double[this.size()];
            for (int i = 0; i < this.size(); i++) {
                fArray[i] = this.get(i).value;
            }
            return fArray;
        }

        public Long[] getTimes() {
            Long[] lArray = new Long[this.size()];
            for (int i = 0; i < this.size(); i++) {
                lArray[i] = this.get(i).time;
            }
            return lArray;
        }

//        public void setTimes(long from, long period) {
//            for (TimeSeries.Entry entry : this) {
//                entry.time = entry.logicalIndex * period + from;
//            }
//        }

        public boolean equals(Object other_obj) {
            if (!(other_obj instanceof DataSequence)) {
                return false;
            }
            DataSequence other = (DataSequence) other_obj;
            if (!super.equals(other)) {
                return false;
            }
            return true;
        }
    }

    public DataSequence data;
    public MetricMeta meta;

    public TimeSeries() {
        this.data = new DataSequence();
        this.meta = new MetricMeta();
    }

    // 新创建的 TimeSeries 对象调用 generate 进行初始化；
    public TimeSeries append(double[] values) {
        data.append(values);
        return this;
    }

    public TimeSeries append(double value) {
        data.append(value);
        return this;
    }

    public TimeSeries append(long[] times, double[] values){
        data.append(times, values);
        return this;
    }

    public TimeSeries append(long time, double value) {
        data.append(time, value);
        return this;
    }

    static public DataSequence generateDataSequence(long from, long period, JSONArray values) {
        DataSequence ds = new DataSequence();

        for (int i = 0; i < values.length() ; i++) {
            try {
                ds.append(from, values.getDouble(i));
                from += period;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return ds;

    }

    static public Long[] generateIndices(long from, long to, long period) {
        Long size = (to - from) / period;
        if (size > Integer.MAX_VALUE)
            new IllegalArgumentException("values size is too large!");

        int len = size.intValue();
        Long[] indices = new Long[len];
        long current = from;
        for (int i = 0; i < len; i++) {
            indices[i] = current;
            current += period;
        }

        return indices;
    }

    @Override
    public JSONObject toJson() {
        return null;
    }

    @Override
    public void fromJson(JSONObject json) {

    }

    // Aggregates time-series based on the specified frequency.
    public DataSequence aggregate(int frequency) {
        DataSequence aggregateData = new DataSequence();

        for (int i = 0; i < data.size(); i += frequency) {
            Double aggregateValue =  0.0;
            int count = 0;
            for (int j = i; j < Math.min(data.size(), (i + frequency)); j++) {
                aggregateValue += data.get(j).value;
                count++;
            }
            aggregateValue = aggregateValue / (float) count;
            aggregateData.append(data.get(i).time, aggregateValue);
        }
        return aggregateData;
    }

    public int size() {
        return data.size();
    }

    public long startTime() {
        return data.get(0).time;
    }

    public long lastTime() {
        return data.get(data.size() - 1).time;
    }

    public long time(int index) {
        return data.get(index).time;
    }

    public double value(int index) {
        return data.get(index).value;
    }

    // may return 0 if size < 2
    public long minimumPeriod() {
        if (size() < 2) {
            return 0;
        }
        long minPeriod = -1;
        for (int i = 1; i < size(); ++i) {
            long period = time(i) - time(i - 1);
            if (minPeriod == -1 || period < minPeriod) {
                minPeriod = period;
            }
        }
        return minPeriod;
    }

    protected class PeriodAndCount {
        public long period = 0;
        public int count = 0;

        public PeriodAndCount(long period_arg, int count_arg) {
            period = period_arg;
            count = count_arg;
        }
    }

    // may return 0 if size < 2
    public long mostFrequentPeriod() {
        if (size() < 2) {
            return 0;
        }
        ArrayList<PeriodAndCount> periods = new ArrayList<PeriodAndCount>();
        // for each time...
        for (int i = 1; i < size(); ++i) {
            // increment period count
            long period = time(i) - time(i - 1);
            boolean found = false;
            for (int p = 0; p < periods.size(); ++p) {
                PeriodAndCount pc = periods.get(p);
                if (pc.period == period) {
                    found = true;
                    ++pc.count;
                }
            }
            if (!found) {
                periods.add(new PeriodAndCount(period, 1));
            }
        }
        // find most frequent period
        int maxCount = 0;
        long maxPeriod = 0;
        for (int p = 0; p < periods.size(); ++p) {
            PeriodAndCount pc = periods.get(p);
            if (pc.count > maxCount) {
                maxCount = pc.count;
                maxPeriod = pc.period;
            }
        }
        return maxPeriod;
    }

    public String toString() {
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < data.size(); ++i) {
            if (i > 0) {
                str.append(",");
            }
            str.append("[" + time(i) + ":" + value(i) + "]");
        }
        return str.toString();
    }

    public boolean equals(Object other_obj) {
        if (!(other_obj instanceof TimeSeries)) {
            return false;
        }
        TimeSeries other = (TimeSeries) other_obj;
        if (!MetricMeta.equals(data, other.data)) {
            return false;
        }
        if (!MetricMeta.equals(meta, other.meta)) {
            return false;
        }
        return true;
    }
}
