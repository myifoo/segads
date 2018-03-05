package com.myitech.segads.services.impl;

import com.myitech.segads.services.ModelService;
import com.myitech.segads.utils.DataSetUtils;
import net.sourceforge.openforecast.*;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Description:
 * <p>
 * Created by A.T on 2018/02/28
 */
@Deprecated
public class ModelServiceImpl implements ModelService{


    @Override
    public JSONObject forecast(DataSet sampleData, DataSet predictData, String forecastType) {
        if (StringUtils.equals(forecastType, ""))
            return bestForecast(sampleData, predictData);

        return null;
    }

    private JSONObject bestForecast(DataSet sampleData, DataSet predictData) {
        JSONObject result = new JSONObject();

        try {
            ForecastingModel forecaster = Forecaster.getBestForecast(sampleData);
            String forecastType = forecaster.getForecastType();

            forecaster.forecast(predictData);
            result.append("predict_series", DataSetUtils.toJSONArray(predictData))
                  .append("forecast_type", forecastType);

            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            // TODO 转换异常类型后抛出，由调用者决定如何进行异常处理，后续通过定义新的 RuntimeException 类型来管理异常类的结构
            throw new RuntimeException("Failed when do bestForecast() : ", e);
        }
    }



}
