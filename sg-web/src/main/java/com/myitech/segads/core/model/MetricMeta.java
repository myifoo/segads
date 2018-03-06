package com.myitech.segads.core.model;

import org.codehaus.jettison.json.JSONObject;

/**
 * Description:
 * <p>
 * Created by A.T on 2018/03/05
 */
public class MetricMeta implements Jsonable {
    public String id;
    public boolean detectAnomalies = false;
    public String name;
    public String fileName;
    public String source;
    public String smoothing;
    public long[] seasons;

    public MetricMeta() {
    }

    public MetricMeta(String id) {
        this.id = id;
    }

    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append("id=" + id);
        str.append(" detectAnomalies=" + detectAnomalies);
        str.append(" name=" + name);
        str.append(" source=" + source);
        return str.toString();
    }

    @Override
    public JSONObject toJson() {
        return null;
    }

    @Override
    public void fromJson(JSONObject json) {

    }

    // needed for unit tests
    public boolean equals(Object other_obj) {
        if (!(other_obj instanceof MetricMeta)) {
            return false;
        }
        MetricMeta other = (MetricMeta) other_obj;
        if (!equals(id, other.id)) {
            return false;
        }
        if (detectAnomalies != other.detectAnomalies) {
            return false;
        }
        if (!equals(name, other.name)) {
            return false;
        }
        if (!equals(source, other.source)) {
            return false;
        }
        return true;
    }

    public static boolean equals(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null && o2 != null) {
            return false;
        }
        if (o1 != null && o2 == null) {
            return false;
        }
        return o1.equals(o2);
    }

}
