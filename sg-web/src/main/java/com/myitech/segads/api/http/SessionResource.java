package com.myitech.segads.api.http;

import com.myitech.segads.core.SessionService;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Path("/session")
public class SessionResource {

    @Inject
    SessionService sessionService;

    @GET
    @Path("/init/{key}")
    public Response init(@PathParam("key")String key) {
        try {
            sessionService.init(key);
            return Response.status(200).entity(key).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/clear/{key}")
    public Response clear(@PathParam("key") String key) {
        try {
            sessionService.destroy(key);
            return Response.status(200).entity(key).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/describe/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    public JSONObject describe(@PathParam("key") String key) {
        return sessionService.describe(key);
    }

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public JSONArray list() {
        return new JSONArray(sessionService.list());
    }

    @POST
    @Path("/{id}/property")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response property(@PathParam("id") String id,
                             @DefaultValue("general") @QueryParam("type")  String type,
                             JSONObject params) {
        try {
            Map<String, String> properties = new HashMap<>();
            Iterator iterator = params.keys();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                properties.put(key, params.getString(key));
            }

            sessionService.property(id, type, properties);

            return Response.status(200).entity(id).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }


    @GET
    @Path("/show")
    @Produces(MediaType.TEXT_PLAIN)
    public String show() {
        return sessionService.show();
    }


    @POST
    @Consumes("application/json")
    @Path("/json")
    public Response postForm(JSONObject jsonObject) throws JSONException {
//        jsonObject.getString("key");
        return Response.status(500).entity("hhh").build();
    }

    @GET
    @Produces("application/json")
    @Path("/jsons")
    public JSONObject getJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key1", "value1");
        jsonObject.put("key2", "value2");

        return jsonObject;
    }

    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public String close() {
        return Long.toString(10002);
    }

    @GET
    @Path("/config")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response config() {
        return Response.status(200).entity("{name:erhei, age:20}").build();
    }


    @GET
    @Path("/info")
    @Produces(MediaType.TEXT_PLAIN)
    //@Produces("text/plain")
    public String info() {
        return "Hello Session";
    }

    // This method is called if XML is request
    @GET
    @Path("/json")
    @Produces(MediaType.APPLICATION_JSON)
    public String sayJsonHello() {
        return "{name:erhei, age:12}";
    }

    // This method is called if XML is request
    @GET
    @Path("/xml")
    @Produces(MediaType.TEXT_XML)
    public String sayXMLHello() {
        return "<?xml version=\"1.0\"?>" + "<hello> Hello simpleRestWebService" + "</hello>";
    }

    // This method is called if HTML is request
    @GET
    @Path("/html")
    @Produces(MediaType.TEXT_HTML)
    public String sayHtmlHello() {
        return "<html> " + "<title>" + "Hello simpleRestWebService" + "</title>"
                + "<body><h1>" + "Hello simpleRestWebService" + "</body></h1>" + "</html> ";
    }
}
