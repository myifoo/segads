package com.myitech.segads.core.forecast;


import net.sourceforge.openforecast.ForecastingModel;

/**
 * Description:
 * <p>
 * Created by A.T on 2018/02/28
 */
public interface TimeSeriesModel {
//    void train() throws Exception;
//    void update() throws Exception;
//    void predict() throws Exception;

    ForecastingModel build();
}
