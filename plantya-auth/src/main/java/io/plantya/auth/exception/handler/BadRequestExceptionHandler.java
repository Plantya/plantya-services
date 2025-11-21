package io.plantya.auth.exception.handler;

import io.plantya.auth.dto.response.ErrorResponse;
import io.plantya.auth.exception.BadRequestException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.LocalDateTime;

@Provider
public class BadRequestExceptionHandler implements ExceptionMapper<BadRequestException> {

    private final UriInfo uriInfo;

    public BadRequestExceptionHandler(@Context UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    @Override
    public Response toResponse(BadRequestException exception) {
        ErrorResponse error = new ErrorResponse(
                uriInfo.getPath(),
                400,
                exception.getMessage(),
                LocalDateTime.now()
        );
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(error)
                .build();
    }
}
