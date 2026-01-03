package io.plantya.user.controller;

import io.plantya.user.common.dto.request.UserQueryParam;
import io.plantya.user.dto.request.UserUpdateRequest;
import io.plantya.user.dto.request.UserCreateRequest;
import io.plantya.user.dto.response.UserUpdateResponse;
import io.plantya.user.dto.response.PagedUserResponse;
import io.plantya.user.dto.response.UserCreateResponse;
import io.plantya.user.dto.response.UserGetResponse;
import io.plantya.user.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

@Path("/api/users")
@Produces("application/json")
@Consumes("application/json")
public class UserController {

    @Inject
    UserService userService;

    @GET
    public Response findAllExistingUsers(@BeanParam UserQueryParam param) {
        PagedUserResponse<UserGetResponse> response = userService.findAllExistingUsers(param);
        return Response.status(Response.Status.OK)
                .entity(response)
                .build();
    }

    @POST
    public Response createUser(UserCreateRequest request) {
        UserCreateResponse response = userService.createUser(request);
        return Response.status(Response.Status.OK)
                .entity(response)
                .build();
    }

    @GET
    @Path("/{userId}")
    public Response findByUserId(@PathParam("userId") String userId) {
        UserGetResponse response = userService.findByUserId(userId);
        return Response.status(Response.Status.OK)
                .entity(response)
                .build();
    }

    @PATCH
    @Path("/{userId}")
    public Response updateUser(@PathParam("userId") String userId, UserUpdateRequest request) {
        UserUpdateResponse response = userService.updateUser(userId, request);
        return Response.status(Response.Status.OK)
                .entity(response)
                .build();
    }

    @DELETE
    @Path("/{userId}")
    public Response deleteUser(@PathParam("userId") String userId) {
        userService.deleteUser(userId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
