package io.plantya.iot.device.controller;

import io.plantya.iot.device.dto.request.DeviceCreateRequest;
import io.plantya.iot.device.dto.request.DeviceUpdateRequest;
import io.plantya.iot.device.dto.response.DeviceCreateResponse;
import io.plantya.iot.device.dto.response.DeviceGetResponse;
import io.plantya.iot.device.dto.response.DeviceUpdateResponse;
import io.plantya.iot.device.dto.response.PagedDeviceResponse;
import io.plantya.iot.device.service.DeviceService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/devices")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DeviceController {

    @Inject
    DeviceService deviceService;

    @GET
    public Response findAllExistingDevices(@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam("search") String search) {
        PagedDeviceResponse response = deviceService.findAllExistingDevices(page, size, search);
        return Response.status(Response.Status.OK)
                .entity(response)
                .build();
    }

    @POST
    public Response createDevice(DeviceCreateRequest request) {
        DeviceCreateResponse response = deviceService.createDevice(request);
        return Response.status(Response.Status.CREATED)
                .entity(response)
                .build();
    }

    @GET
    @Path("/{deviceId}")
    public Response findDeviceById(@PathParam("deviceId") String deviceId) {
        DeviceGetResponse response = deviceService.findDeviceByDeviceId(deviceId);
        return Response.status(Response.Status.OK)
                .entity(response)
                .build();
    }

    @PATCH
    @Path("/{deviceId}")
    public Response updateDevice(@PathParam("deviceId") String deviceId, DeviceUpdateRequest request) {
        DeviceUpdateResponse response = deviceService.updateDevice(deviceId, request);
        return Response.status(Response.Status.OK)
                .entity(response)
                .build();
    }

    @DELETE
    @Path("/{deviceId}")
    public Response deleteDevice(@PathParam("deviceId") String deviceId) {
        deviceService.deleteDevice(deviceId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
