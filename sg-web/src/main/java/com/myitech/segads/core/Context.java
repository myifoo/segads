package com.myitech.segads.core;

/**
 * Description:
 * <p>
 * Created by A.T on 2018/02/03
 */
public interface Context {
    public void register(Object object);
    public void post(Object event);
}
