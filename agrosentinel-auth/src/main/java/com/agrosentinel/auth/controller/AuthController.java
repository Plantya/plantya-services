package com.agrosentinel.auth.controller;

import com.agrosentinel.auth.model.dto.*;
import com.agrosentinel.auth.service.AuthService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

@Path("/api/auth")
@ApplicationScoped
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @GET
    @Path("/ping")
    @Produces(MediaType.TEXT_PLAIN)
    public String ping() {
        return "pong";
    }

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(LoginRequest request) {
        LoginResult result = service.login(request);
        NewCookie cookie = service.createJwtCookie(result.user());

        return Response.ok(result.response()).cookie(cookie).build();
    }

    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public AppResponse<RegisterResponse> register(RegisterRequest request) {
        return service.register(request);
    }

}
