package io.plantya.management.controller;

import io.plantya.management.dto.GetUserDto;
import io.plantya.management.dto.request.CreateUserRequest;
import io.plantya.management.dto.response.UserCreatedResponse;
import io.plantya.management.dto.response.UserResponse;
import io.plantya.management.service.UserService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/v1/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GET
    public Response listUsers() {
        List<UserResponse> userResponseList = service.listUsers();

        return Response.status(Response.Status.OK)
                .entity(userResponseList)
                .build();
    }

    @POST
    public Response createUser(CreateUserRequest request) {
        UserCreatedResponse response = service.createUser(request);
        return Response.status(Response.Status.CREATED)
                .entity(response)
                .build();
    }

    @GET
    @Path("/{userId}")
    public Response getUser(@PathParam("userId") String userId) {
        GetUserDto user = service.getUser(userId);

        return Response.status(Response.Status.OK)
                .entity(user)
                .build();
    }

    @PUT
    @Path("/{userId}")
    public Response replaceUser(@PathParam("userId") String userId) {
        return Response.ok().build();
    }

    @PATCH
    @Path("/{userId}")
    public Response updateUserPartially(@PathParam("userId") String userId) {
        return Response.ok().build();
    }

    @DELETE
    @Path("/{userId}")
    public Response softDeleteUser(@PathParam("userId") String userId) {
        return Response.noContent().build();
    }

    @POST
    @Path("/{userId}/restore")
    public Response restoreUser(@PathParam("userId") String userId) {
        return Response.ok().build();
    }

    @GET
    @Path("/deleted")
    public Response listDeletedUsers() {
        return Response.ok().build();
    }

    @GET
    @Path("/deleted/{userId}")
    public Response getDeletedUser(@PathParam("userId") String userId) {
        return Response.ok().build();
    }

}
