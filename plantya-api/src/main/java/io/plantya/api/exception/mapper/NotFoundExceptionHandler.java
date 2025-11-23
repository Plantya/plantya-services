package io.plantya.api.exception.mapper;

import io.plantya.api.dto.response.ErrorResponse;
import io.plantya.api.exception.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Provider
public class NotFoundExceptionHandler implements ExceptionMapper<NotFoundException> {

    private final UriInfo uriInfo;

    public NotFoundExceptionHandler(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    @Override
    public Response toResponse(NotFoundException e) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        ErrorResponse errorResponse = new ErrorResponse(
                formatter.format(LocalDateTime.now()),
                Response.Status.NOT_FOUND.getStatusCode(),
                uriInfo.getPath(),
                e.getMessage()
        );

        return Response.status(Response.Status.NOT_FOUND)
                .entity(errorResponse)
                .build();
    }

}
