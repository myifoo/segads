package com.myitech.segads.services;

import org.codehaus.jettison.json.JSONObject;

import java.util.Map;

/**
 * Description:
 * <p>
 * Created by A.T on 2018/02/28
 */
public interface ModelService {
    /**
     *  创建模型
     *
     * @param ts 时间序列数据
     * @param properties 建模参数
     * @return json 数据
     */
    JSONObject build();
}
