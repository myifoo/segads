
package com.myitech.segads;


import junit.framework.TestCase;
import org.json.JSONObject;
import org.junit.Test;


public class MainTest extends TestCase {


    public MainTest(String testName) {
        super(testName);
    }

    @Test
    public void testJSON() {
        Double[] array = new Double[]{1.0, 2.0, 3.0, 4.0};
        Double[][] arrays = new Double[][] {{1.0, 2.0, 3.0, 4.0}, {1.0, 2.0, 3.0, 4.0}};

        JSONObject json = new JSONObject(array);
        JSONObject jsonObject = new JSONObject(arrays);

        System.out.println("Hello");
    }



}
