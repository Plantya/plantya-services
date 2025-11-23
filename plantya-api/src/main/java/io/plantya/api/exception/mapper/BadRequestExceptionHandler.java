package io.plantya.api.exception.mapper;

import io.plantya.api.dto.response.ErrorResponse;
import io.plantya.api.exception.BadRequestException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Provider
public class BadRequestExceptionHandler implements ExceptionMapper<BadRequestException> {

    private final UriInfo uriInfo;

    public BadRequestExceptionHandler(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    @Override
    public Response toResponse(BadRequestException e) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        ErrorResponse errorResponse = new ErrorResponse(
                formatter.format(LocalDateTime.now()),
                Response.Status.BAD_REQUEST.getStatusCode(),
                uriInfo.getPath(),
                e.getMessage()
        );

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorResponse)
                .build();
    }
}
