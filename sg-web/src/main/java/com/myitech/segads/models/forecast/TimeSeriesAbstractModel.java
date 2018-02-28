package com.myitech.segads.models.forecast;

import java.util.Properties;

/**
 * Description:
 * <p>
 * Created by A.T on 2018/02/28
 */
abstract public class TimeSeriesAbstractModel implements TimeSeriesModel {
    private static final double TOLERANCE = 0.00000001;

    // Accuracy stats for this model.
    protected double bias;
    protected double mad;
    protected double mape;
    protected double mse;
    protected double sae;
    protected String modelName;

    protected boolean errorsInit = false;
    protected int dynamicParameters = 0;

    // Acts as a factory method.
    public TimeSeriesAbstractModel(Properties config) {
        if (config.getProperty("DYNAMIC_PARAMETERS") != null) {
            this.dynamicParameters = new Integer(config.getProperty("DYNAMIC_PARAMETERS"));
        }

    }

    // 1 when absolute value of error1 is smaller than the absolute value of error2
    // 0 when absolute value of error1 is equal(upto tolerance) to the absolute value of error2
    // -1 when absolute value of error1 is greater than the absolute value of error2
    private static int compareError(double error1, double error2) {
        // can't compare NaN
        if (Double.isNaN(error1) || Double.isNaN(error2)) {
            return 0;
        }
        // positive when error1 is better (smaller) then error2
        double diffAbs = Math.abs(error2) - Math.abs(error1);
        if (Math.abs(diffAbs) <= TOLERANCE) {
            return 0;
        }
        return diffAbs > 0 ? 1 : -1;
    }

    /**
     * Returns the bias - the arithmetic mean of the errors - obtained from applying the current forecasting model to
     * the initial data set to try and predict each data point. The result is an indication of the accuracy of the model
     * when applied to your initial data set - the smaller the bias, the more accurate the model.
     *
     * @return the bias - mean of the errors - when the current model was applied to the initial data set.
     */
    public double getBias() {
        if (errorsInit == false) {
            return -1;
        }
        return bias;
    }

    /**
     * Returns the mean absolute deviation obtained from applying the current forecasting model to the initial data set
     * to try and predict each data point. The result is an indication of the accuracy of the model when applied to your
     * initial data set - the smaller the Mean Absolute Deviation (MAD), the more accurate the model.
     *
     * @return the mean absolute deviation (MAD) when the current model was applied to the initial data set.
     */
    public double getMAD() {
        if (errorsInit == false) {
            return -1;
        }
        return mad;
    }

    /**
     * Returns the mean absolute percentage error obtained from applying the current forecasting model to the initial
     * data set to try and predict each data point. The result is an indication of the accuracy of the model when
     * applied to the initial data set - the smaller the Mean Absolute Percentage Error (MAPE), the more accurate the
     * model.
     *
     * @return the mean absolute percentage error (MAPE) when the current model was applied to the initial data set.
     */
    public double getMAPE() {
        if (errorsInit == false) {
            return -1;
        }
        return mape;
    }

    /**
     * Returns the mean square of the errors (MSE) obtained from applying the current forecasting model to the initial
     * data set to try and predict each data point. The result is an indication of the accuracy of the model when
     * applied to your initial data set - the smaller the Mean Square of the Errors, the more accurate the model.
     *
     * @return the mean square of the errors (MSE) when the current model was applied to the initial data set.
     */
    public double getMSE() {
        if (errorsInit == false) {
            return -1;
        }
        return mse;
    }

    /**
     * Returns the Sum of Absolute Errors (SAE) obtained by applying the current forecasting model to the initial data
     * set. Initialized following a call to init.
     *
     * @return the sum of absolute errors (SAE) obtained by applying this forecasting model to the initial data set.
     */
    public double getSAE() {
        if (!errorsInit) {
            return -1;
        }
        return sae;
    }
}
