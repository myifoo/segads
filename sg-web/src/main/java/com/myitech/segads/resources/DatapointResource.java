package com.myitech.segads.resources;

import com.myitech.segads.data.DataPoint;
import com.myitech.segads.services.DatapointsService;
import org.apache.commons.csv.CSVFormat;
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
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Description:
 * <p>
 * Created by A.T on 2018/02/26
 */
@Path("/data")
public class DatapointResource {
    private Logger logger = LoggerFactory.getLogger(DatapointResource.class);

    @Inject
    DatapointsService service;

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
            List<DataPoint> dataPoints = new LinkedList<>();
            records.forEach((CSVRecord record) -> {
                try {
                    dataPoints.add(new DataPoint(Long.parseLong(record.get(0)), Double.parseDouble(record.get(1))));
                } catch (NumberFormatException e) {
                    logger.error("Error : {}", record.toString());
                }
            });

            service.insert(dataPoints, id, DatapointsService.TYPE.RAW);

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

                service.insert(DataPoint.toList(array), id, DatapointsService.TYPE.RAW);
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
}
