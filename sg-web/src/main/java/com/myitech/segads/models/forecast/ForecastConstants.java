package com.myitech.segads.models.forecast;

/**
 * Description:
 * <p>
 * Created by A.T on 2018/02/28
 */
public class ForecastConstants {
    static public final String INDEPENDENT_VAR_KEY = "time";
    static public final String NOT_TIME_VARIABLE = "not_time_series";

    static public final String SOURCE_ID = "source_series_id";
    static public final String SOURCE_SERIES = "source_series";
    static public final String TYPE = "forecast_type";
    static public final String SOURCE_TYPE = "source_type";
    static public final String SOURCE_DB = "db";
    static public final String SOURCE_JSON = "json";

    static public final String PERIODS_PER_YEAR = "periods_per_year";



    static public final String TARGET_SERIES = "target_series";
    static public final String TARGET_PARAMETERS = "target_parameters";
    static public final String TARGET_PARAMETER_FROM = "from";
    static public final String TARGET_PARAMETER_COUNT = "count";
    static public final String TARGET_PARAMETER_STEP = "step";


    static public final String MODEL_NAME = "forecast_model";
    static public final String MODEL_SIMPLE_EXPONENTIAL_SMOOTHING = "SimpleExponentialSmoothingModel";
    static public final String MODEL_DOUBLE_EXPONENTIAL_SMOOTHING = "DoubleExponentialSmoothingModel";
    static public final String MODEL_TRIPLE_EXPONENTIAL_SMOOTHING = "TripleExponentialSmoothingModel";
}
