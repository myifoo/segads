package com.myitech.segads.core.handler;

import com.myitech.segads.core.SegadsService;
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
