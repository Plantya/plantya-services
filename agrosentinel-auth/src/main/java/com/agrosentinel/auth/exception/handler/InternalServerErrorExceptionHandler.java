package com.agrosentinel.auth.exception.handler;

import com.agrosentinel.auth.dto.response.ErrorResponse;
import com.agrosentinel.auth.exception.InternalServerErrorException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.LocalDateTime;

@Provider
public class InternalServerErrorExceptionHandler implements ExceptionMapper<InternalServerErrorException> {

    private final UriInfo uriInfo;

    public InternalServerErrorExceptionHandler(@Context UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    @Override
    public Response toResponse(InternalServerErrorException exception) {
        ErrorResponse error = new ErrorResponse(
                uriInfo.getPath(),
                500,
                exception.getMessage(),
                LocalDateTime.now()
        );
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(error)
                .build();
    }
}
