package io.plantya.auth.controller;

import io.plantya.auth.dto.request.LoginRequest;
import io.plantya.auth.dto.request.RegisterRequest;
import io.plantya.auth.dto.response.AppResponse;
import io.plantya.auth.dto.response.LoginResult;
import io.plantya.auth.dto.response.RegisterResponse;
import io.plantya.auth.service.AuthService;
import io.plantya.auth.service.CookieService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

@Path("/api/auth")
@ApplicationScoped
public class AuthController {

    private final AuthService authService;
    private final CookieService cookieService;

    public AuthController(AuthService authService, CookieService cookieService) {
        this.authService = authService;
        this.cookieService = cookieService;
    }

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(LoginRequest request) throws Exception {
        LoginResult result = authService.login(request);
        NewCookie cookie = cookieService.createJwtCookie(result.user());

        return Response.ok(result.response()).cookie(cookie).build();
    }

    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public AppResponse<RegisterResponse> register(RegisterRequest request) {
        return authService.register(request);
    }

    @POST
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout() {
        AppResponse<String> body = authService.logout();
        NewCookie deletedCookie = cookieService.deleteCookie();

        return Response.ok(body)
                .cookie(deletedCookie)
                .build();
    }

}
