package com.myitech.segads.core;

import com.myitech.segads.exceptions.LifecycleException;

/**
 * Description:
 * <p>
 * Created by A.T on 2018/02/01
 */
public interface LifeCycle {
    enum Status {WAITING, INIT, RUNNING, FAILED, STOP}

    void init() throws LifecycleException;
    void start() throws LifecycleException;
    void stop();
}
