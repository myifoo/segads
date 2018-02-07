package com.myitech.segads.services.impl;

import com.myitech.segads.services.SegadsService;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Singleton;


/**
 * Created by A.T on 2018/1/9.
 */

@Service
@Singleton
public class SegadsServiceImpl implements SegadsService{
    @Override
    public void command() {

    }

    @Override
    public void configure() {

    }

    @Override
    public void control() {

    }

    @Override
    public String info() {
        return "Hello segads!";
    }
}
