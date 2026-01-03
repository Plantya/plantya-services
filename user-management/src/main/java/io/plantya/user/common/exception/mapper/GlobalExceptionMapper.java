package io.plantya.user.common.exception.mapper;

import io.plantya.user.common.dto.response.ErrorResponse;
import io.plantya.user.common.exception.InternalErrorException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.time.Instant;

import static io.plantya.user.common.exception.message.ErrorMessage.INTERNAL_SERVER_ERROR;


@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class);

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(Throwable throwable) {
        LOG.error("Unhandled exception found", throwable);

        InternalErrorException exception = new InternalErrorException(INTERNAL_SERVER_ERROR);

        ErrorResponse response = new ErrorResponse(
                exception.getDetail(),
                Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                exception.getDetail(),
                uriInfo.getPath(),
                exception.getError().getCode(),
                Instant.now()
        );

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(response)
                .build();
    }
}
