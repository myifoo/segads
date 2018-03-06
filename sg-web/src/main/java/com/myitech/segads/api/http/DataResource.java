package com.myitech.segads.api.http;

import com.myitech.segads.core.DataService;
import com.myitech.segads.core.utils.DataUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Description:
 * <p>
 * Created by A.T on 2018/02/26
 */
@Path("/data")
public class DataResource {
    private Logger logger = LoggerFactory.getLogger(DataResource.class);

    @Inject
    DataService service;

    @POST
    @Path("/csv/create")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response create(@FormDataParam("file") InputStream fileInputStream,
                           @FormDataParam("file") FormDataContentDisposition disposition,
                           @QueryParam("id") String id) {
        logger.debug("upload csv file: {}", disposition.getFileName());

        try {
            InputStreamReader reader = new InputStreamReader(fileInputStream);
            CSVParser records = CSVFormat.EXCEL.parse(reader);
            Object[][] datapoints = new Object[(int)records.getRecordNumber()][2]; // 初始化数组

            Iterator<CSVRecord> iterator = records.iterator();
            for (int i = 0; iterator.hasNext(); i++) {
                CSVRecord record = iterator.next();
                datapoints[i][0] = Long.parseLong(record.get(0));
                datapoints[i][1] = Double.parseDouble(record.get(1));
            }

            service.insert(datapoints,  simpleAttributes(id));
            return Response.status(200).entity("OK").build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    /**
     *      {
     *              metric_01: [[time1, value1], [time2, value2]],
     *              metric_02: [[time1, value1], [time2, value2]]
     *      }
     */
    @POST
    @Path("/json/append")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(JSONObject params) { // 使用 JSONObject 的效率 VS 字符串处理
        try {
            Iterator iterator = params.keys();
            while (iterator.hasNext()) {
                String id = (String) iterator.next();
                JSONArray array = params.getJSONArray(id);

                service.insert(DataUtils.toArrays(array),  simpleAttributes(id));
            }

            return Response.status(200).entity("OK").build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    /**
     *
     *  request content
     *
     *      data: [[1, 2],[2, 2],[3, 3]]
     *      id: test
     *      type: raw
     */
    @POST
    @Path("/form/append")
    @Consumes("application/x-www-form-urlencoded")
    public Response update(MultivaluedMap<String, String> formParams) {
        try {
            service.insert(DataUtils.toArrays(formParams.getFirst("data")),
                    simpleAttributes(formParams.getFirst("id")));

            return Response.status(200).entity("OK").build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    /**
     * {
     *      metric_01: {
     *          from: xxx,
     *          to: xxx,
     *          period: xxx,
     *          values: []
     *      },
     *      metric_02: {
     *          from: xxx,
     *          to: xxx,
     *          period: xxx,
     *          values: []
     *      }
     * }
     */
    @POST
    @Path("/generate")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response generate(JSONObject params) {
        try {
            Iterator<String> iterator = params.keys();
            while (iterator.hasNext()) {
                String id = iterator.next();
                JSONObject format = params.getJSONObject(id);
                long from = format.getLong("from");
                long period = format.getLong("period");
                JSONArray values = format.getJSONArray("values");

                service.insert(DataUtils.generateArrays(from, period, values), simpleAttributes(id));
            }
            return Response.status(200).entity("OK").build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }



    /**
     * response ：{"id_001":[[1001,5],[1002,2.99]]}
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public JSONArray get(@PathParam("id") String id) {
        return DataUtils.toJSONArray(service.get(id));
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

    private Map<String, String> simpleAttributes(String id) {
        Map<String, String> attributes = Collections.emptyMap();
        attributes.put("id", id);
        attributes.put("type", DataService.TYPE.RAW.toString());
        return attributes;
    }
}
