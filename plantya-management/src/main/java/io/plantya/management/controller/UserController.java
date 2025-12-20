package io.plantya.management.controller;

import io.plantya.management.dto.request.UserPatchRequest;
import io.plantya.management.dto.request.UserRequest;
import io.plantya.management.dto.response.*;
import io.plantya.management.enums.UserRole;
import io.plantya.management.service.UserService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GET
    public Response findAllActive(
            @QueryParam("page") Integer page,
            @QueryParam("size") Integer size,
            @QueryParam("sort") String sort,
            @QueryParam("order") String order,
            @QueryParam("search") String search,
            @QueryParam("role") UserRole role
    ) {
        ListUserResponse<UserResponse> userResponseList = service.findAllActive(page, size, sort, order, search, role);

        return Response.status(Response.Status.OK)
                .entity(userResponseList)
                .build();
    }

    @POST
    public Response createUser(UserRequest request) {
        UserCreatedResponse response = service.createUser(request);
        return Response.status(Response.Status.CREATED)
                .entity(response)
                .build();
    }

    @GET
    @Path("/{userId}")
    public Response findById(@PathParam("userId") String userId) {
        Object user = service.findById(userId);

        return Response.status(Response.Status.OK)
                .entity(user)
                .build();
    }

    @PATCH
    @Path("/{userId}")
    public Response updateFields(@PathParam("userId") String userId, UserPatchRequest request) {
        UserUpdatedResponse response = service.patchUser(userId, request);
        return Response.ok()
                .entity(response)
                .build();
    }

    @DELETE
    @Path("/{userId}")
    public Response delete(@PathParam("userId") String userId) {
        service.deleteUser(userId);
        return Response.noContent().build();
    }

    @POST
    @Path("/{userId}/restore")
    public Response restoreUser(@PathParam("userId") String userId) {
        UserResponse response = service.restoreUser(userId);
        return Response.ok()
                .entity(response)
                .build();
    }

    @GET
    @Path("/deleted")
    public Response findAllDeleted(
            @QueryParam("page") Integer page,
            @QueryParam("size") Integer size,
            @QueryParam("sort") String sort,
            @QueryParam("order") String order,
            @QueryParam("search") String search,
            @QueryParam("role") UserRole role
    ) {
        ListUserResponse<UserDeletedResponse> response = service.findAllDeleted(page, size, sort, order, search, role);

        return Response.status(Response.Status.OK)
                .entity(response)
                .build();
    }

    @GET
    @Path("/deleted/{userId}")
    public Response findDeletedById(@PathParam("userId") String userId) {
        UserDeletedResponse response = service.findDeletedById(userId);
        return Response.status(Response.Status.OK)
                .entity(response)
                .build();
    }

}
