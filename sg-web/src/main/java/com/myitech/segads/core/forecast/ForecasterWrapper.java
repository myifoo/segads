package com.myitech.segads.core.forecast;

import com.myitech.segads.core.utils.DataUtils;
import net.sourceforge.openforecast.DataPoint;
import net.sourceforge.openforecast.DataSet;
import net.sourceforge.openforecast.Forecaster;
import net.sourceforge.openforecast.ForecastingModel;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.function.Supplier;

/**
 * Description:
 * <p>
 * Created by A.T on 2018/03/01
 */
public class ForecasterWrapper {
    private final Logger logger = LoggerFactory.getLogger(ForecasterWrapper.class);

    private String type;
    private DataSet source;
    private DataSet target;
    private ForecastingModel forecaster;

    public ForecasterWrapper model(String type) {
        this.type = type;

        try {
            if (StringUtils.isNotEmpty(type)) {

                Class<?> tsModelClass = Class.forName("com.myitech.segads.services.forecast." + type);
                Constructor<?> constructor = tsModelClass.getConstructor();
                TimeSeriesModel model = (TimeSeriesModel) constructor.newInstance();

                forecaster = model.build();

                this.type = forecaster.getForecastType();
            }
        } catch (Exception e) {
            logger.error("build model failed ", e);
        }

        return this;
    }

    public ForecasterWrapper init(DataSet source) {
        this.source = source;
        if (forecaster == null)
            forecaster = Forecaster.getBestForecast(source);

        forecaster.init(source);
        return this;
    }

    /**
     * @param supplier lambda 表达式形式进行初始化
     */
    public ForecasterWrapper init(Supplier<DataSet> supplier) {
        this.source = supplier.get();
        if (forecaster == null)
            forecaster = Forecaster.getBestForecast(source);

        forecaster.init(source);
        return this;
    }

    public ForecasterWrapper forecast(DataSet target) {
        if (target == null)
            target = new DataSet(source); // 这里必须使用全复制，否则在 forecast 时会覆盖掉 source 的信息；

        this.target = forecaster.forecast(target);

        return this;
    }

    public double forecast(DataPoint dataPoint) {
        return forecaster.forecast(dataPoint);
    }

    public ForecasterWrapper setPeriodsPerYear(int periods) {
        source.setPeriodsPerYear(periods);
        return this;
    }

    public ForecasterWrapper setTimeVariable(String timeVariable) {
        source.setTimeVariable(timeVariable);
        return this;
    }

    public JSONObject toJSONObject() {
        try {
            // TODO 前期为了测试需要返回所有的信息，后期根据情况调整，否则会造成 response 总是携带大量冗余信息！
            JSONObject result = new JSONObject().put(ForecastConstants.TYPE, type)
                    .put(ForecastConstants.SOURCE_SERIES, DataUtils.toJSONArray(source))
                    .put(ForecastConstants.TARGET_SERIES, DataUtils.toJSONArray(target));

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            // TODO 转换异常类型后抛出，由调用者决定如何进行异常处理，后续通过定义新的 RuntimeException 类型来管理异常类的结构
            throw new RuntimeException("Failed when do bestForecast() : ", e);
        }
    }

    public String getType() {
        return type;
    }

    public DataSet getSource() {
        return source;
    }

    public DataSet getTarget() {
        return target;
    }

    public void setSource(DataSet source) {
        this.source = source;
    }

    public void setTarget(DataSet target) {
        this.target = target;
    }
}
