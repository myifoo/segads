package com.myitech.segads.core.events;

/**
 * Description:
 * <p>
 * Created by A.T on 2018/02/02
 */
public class StartSuccessEvent {
    private String name;

    public StartSuccessEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
