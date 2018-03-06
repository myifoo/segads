package com.myitech.segads.api.http;

//@Path("/analysis")
public class AnalysisResource {

    public String acf() {
        return "acf";
    }

    public String pacf() {
        return "pacf";
    }

    public String diff() {
        return "diff";
    }

}
