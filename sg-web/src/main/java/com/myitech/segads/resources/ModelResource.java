package com.myitech.segads.resources;

import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/model")
public class ModelResource {

    @GET
    @Path("/build/{}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JSONObject build(@PathParam("id") String id,
            JSONObject properties) {


        return null;
    }
}
