package io.plantya.auth.exception.handler;

import io.plantya.auth.dto.response.ErrorResponse;
import io.plantya.auth.exception.InvalidCredentialsException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.LocalDateTime;

@Provider
public class InvalidCredentialsExceptionHandler implements ExceptionMapper<InvalidCredentialsException> {

    private final UriInfo uriInfo;

    public InvalidCredentialsExceptionHandler(@Context UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    @Override
    public Response toResponse(InvalidCredentialsException exception) {
        ErrorResponse error = new ErrorResponse(
                uriInfo.getPath(),
                401,
                exception.getMessage(),
                LocalDateTime.now()
        );
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(error)
                .build();
    }
}
