package com.myitech.segads.resources;

import com.myitech.segads.data.RecordValue;
import com.myitech.segads.services.DataService;
import net.sourceforge.openforecast.DataPoint;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Description:
 * <p>
 * Created by A.T on 2018/02/26
 */
@Path("/data")
public class DataResource {
    private Logger logger = LoggerFactory.getLogger(DataResource.class);

    enum Pattern{
        HOUR, DAY, WEEK, MONTH, YEAR
    }

    @Inject
    DataService service;

    @POST
    @Path("/create")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response create(@FormDataParam("file") InputStream fileInputStream,
                           @FormDataParam("file") FormDataContentDisposition disposition,
                           @QueryParam("id") String id) {
        logger.debug("upload data file: {}", disposition.getFileName());

        try {
            InputStreamReader reader = new InputStreamReader(fileInputStream);
            Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(reader);
            List<RecordValue> recordValues = new LinkedList<>();
            records.forEach((CSVRecord record) -> {
                try {
                    recordValues.add(new RecordValue(Long.parseLong(record.get(0)), Double.parseDouble(record.get(1))));
                } catch (NumberFormatException e) {
                    logger.error("Error : {}", record.toString());
                }
            });

            service.insert(recordValues, id, DataService.TYPE.RAW);

            return Response.status(200).entity("OK").build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    /**
     * {
     * id01: [[time1, value1], [time2, value2]],
     * id02: [[time1, value1], [time2, value2]]
     * }
     */
    @POST
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(JSONObject params) {
        try {
            Iterator iterator = params.keys();
            while (iterator.hasNext()) {
                String id = (String) iterator.next();
                JSONArray array = params.getJSONArray(id);

                service.insert(RecordValue.toList(array), id, DataService.TYPE.RAW);
            }

            return Response.status(200).entity("OK").build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    /**
     * 返回的 json 形式为 ：{"id_001":[[1001,5],[1002,2.99]]}
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public JSONArray get(@PathParam("id") String id) {
        return service.get(id);
    }

    @GET
    @Path("/{id}/delete")
    public Response delete(@PathParam("id") String id) {
        try {
            service.delete(id);
            return Response.status(200).entity("OK").build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/do/aggregate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response aggregate(@QueryParam("id") String id,
                              @QueryParam("pattern") int pattern,
                              @QueryParam("push") String tid) {
        try {
            JSONArray result = null;

            switch (Pattern.values()[pattern]) {
                case WEEK:
                    result = aggregateByWeek(service.getList(id));
            }

            if (StringUtils.isNotEmpty(tid)) {
                service.insert(RecordValue.toList(result), tid, DataService.TYPE.RAW);
                return Response.status(200).entity("OK").build();
            }


            return Response.status(200).entity(result).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/do/average")
    @Produces(MediaType.APPLICATION_JSON)
    public Response average(@QueryParam("id") String id,
                              @QueryParam("pattern") int pattern,
                              @QueryParam("push") String tid) {
        try {
            JSONArray result = null;

            switch (Pattern.values()[pattern]) {
                case WEEK:
                    result = averageByWeek(service.getList(id));
            }

            if (StringUtils.isNotEmpty(tid)) {
                service.insert(RecordValue.toList(result), tid, DataService.TYPE.RAW);
                return Response.status(200).entity("OK").build();
            }


            return Response.status(200).entity(result).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

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
