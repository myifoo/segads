package com.myitech.segads.server.events;

/**
 * Description:
 * <p>
 * Created by A.T on 2018/02/07
 */
public class DatabaseUnavailableEvent {
    String message;

    public DatabaseUnavailableEvent(String message) {
        this.message = message;
    }
}
