package io.plantya.api.controller;

import io.plantya.api.dto.SensorDataDTO;
import io.plantya.api.service.HistoricalService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.List;

@Path("/api/history")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({ "ADMIN", "STAFF", "USER" })
public class HistoricalController {

    private final HistoricalService service;

    public HistoricalController(HistoricalService service) {
        this.service = service;
    }

    @GET
    public Response getHistory(
            @QueryParam("deviceId") String deviceId,
            @QueryParam("from") LocalDate from,
            @QueryParam("to") LocalDate to
    ) {
        List<SensorDataDTO> responses = service.getHistoricalData(deviceId, from, to);
        return Response.ok(responses).build();
    }

    @GET
    @Path("/latest")
    public Response getLatestHistory(@QueryParam("deviceId") String deviceId) {
        SensorDataDTO response = service.getLatestData(deviceId);
        return Response.ok(response).build();
    }

}
