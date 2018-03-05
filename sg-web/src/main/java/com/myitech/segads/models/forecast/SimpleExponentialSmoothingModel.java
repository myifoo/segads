package com.myitech.segads.models.forecast;

import net.sourceforge.openforecast.ForecastingModel;

/**
 * Description:
 * <p>
 * Created by A.T on 2018/03/02
 */
public class SimpleExponentialSmoothingModel implements TimeSeriesModel{
    ForecastingModel forecaster;

    public SimpleExponentialSmoothingModel() {
        forecaster = new net.sourceforge.openforecast.models.SimpleExponentialSmoothingModel(0.75);
    }

    public ForecastingModel build() {
        return forecaster;
    }

}
