package com.myitech.segads.services;

import org.jvnet.hk2.annotations.Contract;

/**
 * Created by A.T on 2018/1/9.
 */
@Contract
public interface SegadsService {
    /**
     *
     */
    void command();
    void configure();
    void control();
    String info();
}
