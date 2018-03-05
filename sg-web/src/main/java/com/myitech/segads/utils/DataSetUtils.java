package com.myitech.segads.utils;

import com.myitech.segads.models.forecast.ForecastConstants;
import net.sourceforge.openforecast.DataPoint;
import net.sourceforge.openforecast.DataSet;
import net.sourceforge.openforecast.Observation;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

/**
 * Description:
 * <p>
 * Created by A.T on 2018/02/28
 */
public class DataSetUtils {

    static public JSONArray toJSONArray(DataSet ds) {
        JSONArray result = new JSONArray();

        ds.iterator().forEachRemaining((dp)->  {
            try {
                result.put( new JSONArray().put(dp.getIndependentValue(ForecastConstants.INDEPENDENT_VAR_KEY)).put(dp.getDependentValue()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        return result;
    }

    /**
     *  将 JSONArray 对象转换为 DataSet； JSON 的处理使用了已有的库，后续如果要定制化的优化其性能，可以记性适当的扩展。
     * @param array
     * @return
     */
    static public DataSet fromJSONArray(JSONArray array){
        DataSet dataSet = new DataSet();

        for (int i = 0; i < array.length() ; i++) {
            try {
                JSONArray data = array.getJSONArray(i);

                DataPoint dp = new Observation(data.getDouble(1));
                dp.setIndependentValue(ForecastConstants.INDEPENDENT_VAR_KEY, data.getDouble(0));
                dataSet.add(dp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return dataSet;
    }
}
