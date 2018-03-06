package com.myitech.segads.api.http;

import net.sourceforge.openforecast.DataPoint;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

import javax.ws.rs.Path;
import java.util.List;

/**
 * Description:
 * <p>
 * Created by A.T on 2018/03/06
 */
@Path("/etl")
public class EtlResource {
    //
//    @GET
//    @Path("/math/aggregate")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response aggregate(@QueryParam("id") String id,
//                              @QueryParam("pattern") int pattern) {
//        try {
//            JSONArray result = null;
//
//            switch (Pattern.values()[pattern]) {
//                case WEEK:
//                    result = aggregateByWeek(service.getList(id));
//            }
//
//            return Response.status(200).entity(result).build();
//        } catch (Exception e) {
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }
//
//    @GET
//    @Path("/math/average")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response average(@QueryParam("id") String id,
//                              @QueryParam("pattern") int pattern,
//                              @QueryParam("push") String tid) {
//        try {
//            JSONArray result = null;
//
//            switch (Pattern.values()[pattern]) {
//                case WEEK:
//                    result = averageByWeek(service.getList(id));
//            }
//
//            return Response.status(200).entity(result).build();
//        } catch (Exception e) {
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }

    private JSONArray aggregateByWeek(List<DataPoint> list) {
        JSONArray result = new JSONArray();
        double period = 7*24*3600*1000;


        try {
            double start = list.get(0).getIndependentValue("time");
            double sum = 0;

            for (int i = 0; i < list.size(); i++) {
                DataPoint dp = list.get(i);
                double current = dp.getIndependentValue("time");

                if (current - start > period) {
                    result.put(new JSONArray().put(start - start%period).put(sum));
                    start = current;
                    sum = dp.getDependentValue();
                } else {
                    sum += dp.getDependentValue();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return result;
    }

    private JSONArray averageByWeek(List<DataPoint> list) {
        JSONArray result = new JSONArray();
        double period = 7*24*3600*1000;


        try {
            double start = list.get(0).getIndependentValue("time");
            start = start - start%period; // 数值对齐
            double sum = 0;

            int j = 1;
            for (int i = 0; i < list.size(); i++) {
                DataPoint dp = list.get(i);

                if (dp.getIndependentValue("time") - start > period) {
                    result.put(new JSONArray().put(start).put(sum/j));
                    start += period;
                    sum = 0;
                    j = 0;
                }
                sum += dp.getDependentValue();
                j++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return result;
    }
}
