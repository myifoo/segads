package com.myitech.segads.core;

import net.sourceforge.openforecast.DataSet;
import org.codehaus.jettison.json.JSONObject;

/**
 * Description:
 * <p>
 * Created by A.T on 2018/02/28
 */
public interface ModelService {
    /**
     *  data forecast
     *
     * @return json 数据
     */
    JSONObject forecast(DataSet sampleData, DataSet predictData, String forecastType);
}
