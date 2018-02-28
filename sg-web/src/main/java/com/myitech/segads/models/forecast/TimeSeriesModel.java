package com.myitech.segads.models.forecast;


/**
 * Description:
 * <p>
 * Created by A.T on 2018/02/28
 */
public interface TimeSeriesModel {
    void train() throws Exception;
    void update() throws Exception;
    void predict() throws Exception;
}
