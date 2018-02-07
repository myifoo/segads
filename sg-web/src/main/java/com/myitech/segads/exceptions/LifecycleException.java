package com.myitech.segads.exceptions;

/**
 * Description:
 * <p>
 * Created by A.T on 2018/02/02
 */
public class LifecycleException extends Exception {
    public LifecycleException(String message) {
        super(message);
    }

    public LifecycleException(Throwable cause) {
        super(cause);
    }
}