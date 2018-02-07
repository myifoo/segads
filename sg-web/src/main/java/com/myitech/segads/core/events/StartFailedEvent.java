package com.myitech.segads.core.events;

/**
 * Description:
 * <p>
 * Created by A.T on 2018/02/02
 */
public class StartFailedEvent {
    private Runnable server;
    private String name;

    public StartFailedEvent(Runnable server, String name) {
        this.server = server;
        this.name = name;
    }

    public Runnable getServer() {
        return server;
    }

    public String getName() {
        return name;
    }
}
