package io.plantya.iot.common.validator;

import io.plantya.iot.cluster.dto.request.ClusterCreateRequest;
import io.plantya.iot.common.exception.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestValidatorTest {

    @Nested
    @DisplayName("Validate Cluster Create Request")
    class ValidateClusterCreateRequest {

        @Test
        @DisplayName("Should pass validation when request is valid")
        void shouldPassWhenValidRequest() {
            ClusterCreateRequest request = new ClusterCreateRequest("Test Cluster");

            assertDoesNotThrow(() ->
                    RequestValidator.validateClusterCreateRequest(request)
            );
        }

        @Test
        @DisplayName("Should throw BadRequestException when cluster name is null")
        void shouldThrowWhenClusterNameIsNull() {
            BadRequestException ex = assertThrows(
                    BadRequestException.class,
                    () -> RequestValidator.validateClusterCreateRequest(
                            new ClusterCreateRequest(null)
                    )
            );

            assertEquals("CLUSTER_REQUEST_INVALID", ex.getError().getCode());
        }

        @Test
        @DisplayName("Should throw BadRequestException when cluster name is blank")
        void shouldThrowWhenClusterNameIsBlank() {
            BadRequestException ex = assertThrows(
                    BadRequestException.class,
                    () -> RequestValidator.validateClusterCreateRequest(
                            new ClusterCreateRequest("   ")
                    )
            );

            assertEquals("CLUSTER_NAME_REQUIRED", ex.getError().getCode());
        }
    }
}
