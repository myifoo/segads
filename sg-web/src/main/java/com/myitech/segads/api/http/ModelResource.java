package com.myitech.segads.api.http;

import com.myitech.segads.core.forecast.ForecasterWrapper;
import com.myitech.segads.core.forecast.ForecastConstants;
import com.myitech.segads.core.DataService;
import com.myitech.segads.core.utils.DataUtils;
import net.sourceforge.openforecast.DataPoint;
import net.sourceforge.openforecast.DataSet;
import net.sourceforge.openforecast.Observation;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;

@Path("/model")
public class ModelResource {

    @Inject
    private DataService dataService;

    @POST
    @Path("/forecast")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response forecast(JSONObject info) {
        try {
            ForecasterWrapper forecaster = new ForecasterWrapper().model(info.optString(ForecastConstants.TYPE));

            // 使用 lambda 表达式的好处是大量减少信息传递（函数参数），提高了代码复用度，但同时也多少会降低一些可读性；
            if (StringUtils.isEmpty(info.optString(ForecastConstants.SOURCE_TYPE))) { // 默认为 from db
                String sourceId = info.getString(ForecastConstants.SOURCE_ID);
                forecaster.init(()->  toDataSet(dataService.get(sourceId)));
            } else {
                JSONArray  source = info.getJSONArray(ForecastConstants.SOURCE_SERIES);
                forecaster.init(()->  DataUtils.toDataSet(source));
            }

            int periods = info.optInt(ForecastConstants.PERIODS_PER_YEAR);
            if (periods > 1) forecaster.setPeriodsPerYear(periods);

            boolean isTimeSeries = info.optBoolean(ForecastConstants.NOT_TIME_VARIABLE);
            if (!isTimeSeries) forecaster.setTimeVariable(ForecastConstants.INDEPENDENT_VAR_KEY);

            JSONObject target = info.optJSONObject(ForecastConstants.TARGET_PARAMETERS); // 可以为 null
            JSONObject result = forecaster.forecast(parsePredictParams(target)).toJSONObject();

            return Response.status(200).entity(result).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    /**
     *  时间序列源数据通过接口以 json 的形式提供；
     */
    private JSONObject forecastJson(JSONObject info) throws JSONException {
        JSONArray  source = info.getJSONArray(ForecastConstants.SOURCE_SERIES);
        JSONObject target = info.getJSONObject(ForecastConstants.TARGET_PARAMETERS);

        return new ForecasterWrapper().model(info.getString(ForecastConstants.TYPE))
                                                .init(DataUtils.toDataSet(source))
                                                .forecast(parsePredictParams(target))
                                                .toJSONObject();
    }

    /**
     *  时间序列源数据根据接口提供的 id 从数据库中提取；
     */
    private JSONObject forecastDb(JSONObject info) throws JSONException {
        String sourceId = info.getString(ForecastConstants.SOURCE_ID);
        JSONObject target = info.getJSONObject(ForecastConstants.TARGET_PARAMETERS);

        return new ForecasterWrapper().model(info.getString(ForecastConstants.TYPE))
                                                .init(toDataSet(dataService.get(sourceId)))
                                                .forecast(parsePredictParams(target))
                                                .toJSONObject();

    }

    /**
     * 构造待预测的 DataSet
     */
    private DataSet parsePredictParams(JSONObject params) {
        DataSet ds = null;

        if (params != null) {
            try {
                double from = params.getDouble(ForecastConstants.TARGET_PARAMETER_FROM);
                double step = params.getDouble(ForecastConstants.TARGET_PARAMETER_STEP);
                int count = params.getInt(ForecastConstants.TARGET_PARAMETER_COUNT);

                ds = new DataSet();
                for (int i = 0; i < count; i++) {
                    DataPoint dp = new Observation(0.0);
                    dp.setIndependentValue(ForecastConstants.INDEPENDENT_VAR_KEY, from + i*step);
                    ds.add(dp);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return ds;
    }


    // 将从数据库获取的二维数组转换为 DataSet
    static public DataSet toDataSet(Object[][] source) {
        DataSet ds = new DataSet();
        Arrays.stream(source).forEach((data)-> {
            DataPoint dp = new Observation((long)data[0]);
            dp.setIndependentValue("time", (double)data[1]);
            ds.add(dp);
        });

        return ds;
    }
}
