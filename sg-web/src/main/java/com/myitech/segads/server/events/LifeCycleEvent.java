package com.myitech.segads.server.events;

/**
 * Description:
 * <p>
 * Created by A.T on 2018/02/02
 */
public class LifeCycleEvent {
    private Runnable server;
    private String message;

    public LifeCycleEvent(Runnable server, String message) {
        this.server = server;
        this.message = message;
    }

    public Runnable getServer() {
        return server;
    }

    public String getMessage() {
        return message;
    }
}
