package com.myitech.segads.core;

import java.util.Map;

/**
 * Description:
 *
 *      1.  使用 Java 数组对数据进行存储，在读取、遍历上比 list 更高效、简洁和可扩展。不要在DataService中引入其他数据结构，保证数据
 *          结构的单一性。
 *
 * Created by A.T on 2018/02/26
 */
public interface DataService {
    enum TYPE {
        RAW("raw"),
        TEMP("temp");

        private String name;
        TYPE(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    /**
     * insert data points into database,
     *
     * @param datapoints time-series data points
     * @param attributes  such as {id: xxx, type: xxx}
     */
    void insert(Object[][] datapoints, Map<String, String> attributes);

    /**
     *  get all data points by id
     *
     * @param id table record id
     * @return Object[][]
     */
    Object[][] get(String id);


    /**
     * delete specific data by id
     * @param id table record id
     */
    void delete(String id);
}
