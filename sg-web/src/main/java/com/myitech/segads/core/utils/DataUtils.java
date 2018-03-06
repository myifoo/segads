package com.myitech.segads.core.utils;

import com.myitech.segads.core.forecast.ForecastConstants;
import net.sourceforge.openforecast.DataPoint;
import net.sourceforge.openforecast.DataSet;
import net.sourceforge.openforecast.Observation;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

import java.util.Arrays;

/**
 * Description:
 * <p>
 * Created by A.T on 2018/02/28
 */
public class DataUtils {
    /**
     *  convert DataSet object to JSONArray
     *
     * @param ds DataSet
     * @return JSONArray
     */
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
     *  convert JSONArray object to DataSet object
     *
     * @param array JSONArray
     * @return DataSet
     */
    static public DataSet toDataSet(JSONArray array){
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

    /**
     *  convert String object to Object[][] object
     *
     * @param json JSONArray
     * @return DataSet
     */
    static public Object[][] toArrays(String json) {
        String[] datapoints = StringUtils.split(json,"]");
        int len = datapoints.length;
        Object[][] targetArray = new Object[len - 1][2];

        for (int i = 0; i < len; i++) {
            String[] str = datapoints[i].split(",");

            if (str.length == 2) {
                targetArray[i][0] = Long.parseLong(StringUtils.substringAfterLast(str[0], "["));
                targetArray[i][1] = Double.parseDouble(str[2]);
            } else if (str.length == 3) {
                targetArray[i][0] = Long.parseLong(StringUtils.substringAfterLast(str[1], "["));
                targetArray[i][1] = Double.parseDouble(str[2]);
            }

        }

        return targetArray;
    }

    /**
     *
     * convert String object to Object[][] object
     *
     * @param jsonArray json
     * @return Object[][]
     * @throws JSONException e
     */
    static public Object[][] toArrays(JSONArray jsonArray) throws JSONException {
        int len = jsonArray.length();
        Object[][] targetArray = new Object[len][2];

        for (int i = 0; i < len; i++) {
            targetArray[i][0] = jsonArray.getLong(0);
            targetArray[i][1] = jsonArray.getDouble(1);
        }

        return targetArray;
    }

    /**
     * convert String object to Object[][] object
     *
     * @param source source
     * @return jsonArray JSONArray
     */
    static public JSONArray toJSONArray(Object[][] source) {
        JSONArray target = new JSONArray();

        Arrays.stream(source).forEach((data) -> {
            JSONArray array = new JSONArray();
            array.put(data[0]);
            array.put(data[1]);

            target.put(array);
        });

        return target;
    }

    /**
     *
     * generateArrays by specific rules
     *
     * @param from first timestamp
     * @param period step
     * @param values datapoints
     * @return Object[][]
     *
     * @throws JSONException e
     */
    static public Object[][] generateArrays(long from, long period, JSONArray values)  throws JSONException {
        int len = values.length();
        Object[][] targetArray = new Object[len][2];

        for (int i = 0; i < len; i++, from+=period) {
            targetArray[i][0] = from;
            targetArray[i][1] = values.getDouble(i);
        }

        return targetArray;
    }
}
