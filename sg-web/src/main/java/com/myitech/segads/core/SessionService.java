package com.myitech.segads.core;

import com.myitech.segads.exceptions.InternalDatastoreException;
import org.codehaus.jettison.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by A.T on 2018/1/17.
 */
public interface SessionService {
    /**
     * Firstly, create and init a new session for analysis.
     *
     * @param key session id
     * @exception InternalDatastoreException if init failed
     *
     */
    void init(String key);

    /**
     * fetch all available session ids in system.
     *
     * @return session id list.
     */
    List<String> list();

    /**
     * Destroy the session and clear related resources!
     *
     * @param  key session id
     */
    void destroy(String key);

    /**
     *
     */
    JSONObject describe(String key);

    /**
     * Configure session with properties!
     *
     * @param  properties input properties
     */
    void property(String key, String type, Map<String, String> properties);

    String show();
}
