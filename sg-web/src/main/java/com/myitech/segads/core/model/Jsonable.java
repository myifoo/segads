package com.myitech.segads.core.model;

import org.codehaus.jettison.json.JSONObject;

/**
 * Description:
 * <p>
 * Created by A.T on 2018/03/05
 */
public interface Jsonable {
    JSONObject toJson();
    void fromJson(JSONObject json);
}
