package io.plantya.api.exception.mapper;

import io.plantya.api.dto.response.ErrorResponse;
import io.plantya.api.exception.UnauthorizedException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Provider
public class UnauthorizedExceptionHandler implements ExceptionMapper<UnauthorizedException> {

    private final UriInfo uriInfo;

    public UnauthorizedExceptionHandler(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    @Override
    public Response toResponse(UnauthorizedException e) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        ErrorResponse errorResponse = new ErrorResponse(
                formatter.format(LocalDateTime.now()),
                Response.Status.UNAUTHORIZED.getStatusCode(),
                uriInfo.getPath(),
                e.getMessage()
        );

        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(errorResponse)
                .build();
    }
}
