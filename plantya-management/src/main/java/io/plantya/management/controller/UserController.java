package io.plantya.management.controller;

import io.plantya.management.dto.request.UserRequest;
import io.plantya.management.dto.response.ListUserResponse;
import io.plantya.management.dto.response.UserCreatedResponse;
import io.plantya.management.dto.response.UserResponse;
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
        UserResponse user = service.findById(userId);

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
    public Response updateFields(@PathParam("userId") String userId) {
        return Response.ok().build();
    }

    @DELETE
    @Path("/{userId}")
    public Response delete(@PathParam("userId") String userId) {
        return Response.noContent().build();
    }

    @POST
    @Path("/{userId}/restore")
    public Response restoreUser(@PathParam("userId") String userId) {
        return Response.ok().build();
    }

    @GET
    @Path("/deleted")
    public Response findAllDeleted() {
        return Response.ok().build();
    }

    @GET
    @Path("/deleted/{userId}")
    public Response findDeletedById(@PathParam("userId") String userId) {
        return Response.ok().build();
    }

}
