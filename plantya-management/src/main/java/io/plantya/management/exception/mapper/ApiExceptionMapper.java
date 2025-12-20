package io.plantya.management.exception.mapper;

import io.plantya.management.dto.response.ErrorResponse;
import io.plantya.management.exception.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.time.Instant;

@Provider
public class ApiExceptionMapper implements ExceptionMapper<ApiException> {

    private static final Logger LOG = Logger.getLogger(ApiExceptionMapper.class);

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(ApiException e) {
        Response.Status status = getStatus(e);

        if (status == Response.Status.INTERNAL_SERVER_ERROR) {
            LOG.errorf(
                    "event=system_exception status=500 path=%s",
                    uriInfo.getPath()
            );
            LOG.error("stacktrace", e);
        } else {
            LOG.warnf(
                    "event=business_exception status=%d code=%s path=%s message=\"%s\"",
                    status.getStatusCode(),
                    e.getError().getCode(),
                    uriInfo.getPath(),
                    e.getDetail()
            );
        }

        ErrorResponse response = new ErrorResponse(
                status.getReasonPhrase(),
                status.getStatusCode(),
                e.getDetail(),
                uriInfo.getPath(),
                e.getError().getCode(),
                Instant.now()
        );

        return Response.status(status)
                .entity(response)
                .build();
    }

    private Response.Status getStatus(ApiException e) {
        return switch (e) {
            case BadRequestException ignored -> Response.Status.BAD_REQUEST;
            case NotFoundException ignored -> Response.Status.NOT_FOUND;
            case ConflictException ignored -> Response.Status.CONFLICT;
            default -> Response.Status.INTERNAL_SERVER_ERROR;
        };
    }

}
