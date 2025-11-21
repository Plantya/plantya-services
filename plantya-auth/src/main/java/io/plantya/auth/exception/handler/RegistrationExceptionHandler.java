package io.plantya.auth.exception.handler;

import io.plantya.auth.dto.response.ErrorResponse;
import io.plantya.auth.exception.RegistrationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.LocalDateTime;

@Provider
public class RegistrationExceptionHandler implements ExceptionMapper<RegistrationException> {

    private final UriInfo uriInfo;

    public RegistrationExceptionHandler(@Context UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    @Override
    public Response toResponse(RegistrationException exception) {
        ErrorResponse error = new ErrorResponse(
                uriInfo.getRequestUri().toString(),
                400,
                exception.getMessage(),
                LocalDateTime.now()
        );
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(error)
                .build();
    }
}