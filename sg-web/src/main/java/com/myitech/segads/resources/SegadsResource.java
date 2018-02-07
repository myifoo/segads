package com.myitech.segads.resources;

import com.myitech.segads.Segads;
import com.myitech.segads.core.events.LifeCycleEvent;
import com.myitech.segads.services.SegadsService;
import org.glassfish.hk2.api.messaging.Topic;
import org.glassfish.jersey.server.mvc.Viewable;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/")
public class SegadsResource {

    @Inject
    SegadsService segadsService;

    @GET
    @Path("info")
    public String info() {
        return segadsService.info();
    }

    @GET
    @Path("hello")
    @Produces(MediaType.TEXT_HTML)
    public Viewable getHello() {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("user", "Pavel");
        final List<String> list = new ArrayList<String>();
        list.add("item1");
        list.add("item2");
        list.add("item3");
        map.put("items", list);

        return new Viewable("/hello.ftl", map);
    }

    @GET
    @Path("help")
    @Produces(MediaType.TEXT_HTML)
    public Viewable help() {
        return null;
    }

    @GET
    @Path("stop")
    public String stop() {
        Segads.post(new LifeCycleEvent(null, "Hello Event!"));
        return "stopEvent";
    }

    @GET
    @Path("query")
    @Produces(MediaType.TEXT_HTML)
    public Viewable query() {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("user", "Pavel");
        final List<String> list = new ArrayList<String>();
        list.add("item1");
        list.add("item2");
        list.add("item3");
        map.put("items", list);

        return new Viewable("/help.ftl", map);
    }

}