package com.myitech.segads.resources;

import javax.ws.rs.Path;

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
