package io.plantya.user.controller;

import io.plantya.user.common.dto.request.UserQueryParam;
import io.plantya.user.dto.response.PagedUserResponse;
import io.plantya.user.dto.response.UserDeletedResponse;
import io.plantya.user.dto.response.UserGetResponse;
import io.plantya.user.service.UserDeletedService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

@Path("/api/users/deleted")
@Produces("application/json")
@Consumes("application/json")
public class UserDeletedController {

    @Inject
    UserDeletedService userDeletedService;

    @GET
    public Response findAllDeletedUsers(@BeanParam UserQueryParam param) {
        PagedUserResponse<UserDeletedResponse> response = userDeletedService.findAllDeletedUsers(param);
        return Response.status(Response.Status.OK)
                .entity(response)
                .build();
    }

    @GET
    @Path("/{userId}")
    public Response findDeletedByUserId(@PathParam("userId") String userId) {
        UserDeletedResponse response = userDeletedService.findDeletedByUserId(userId);
        return Response.status(Response.Status.OK)
                .entity(response)
                .build();
    }

    @POST
    @Path("/{userId}/restore")
    public Response restoreUser(@PathParam("userId") String userId) {
        UserGetResponse response = userDeletedService.restoreUser(userId);
        return Response.status(Response.Status.CREATED)
                .entity(response)
                .build();
    }
}
