package io.plantya.iot.common.validator;

import io.plantya.iot.cluster.dto.request.ClusterCreateRequest;
import io.plantya.iot.cluster.dto.request.ClusterUpdateRequest;
import io.plantya.iot.common.exception.BadRequestException;
import io.plantya.iot.device.dto.request.DeviceCreateRequest;

import static io.plantya.iot.common.exception.message.ErrorMessage.*;

public class RequestValidator {

    public static void validateDeviceCreateRequest(DeviceCreateRequest request) {
        if (request == null) {
            throw new BadRequestException(DEVICE_REQUEST_INVALID);
        }

        if (isBlank(request.deviceName())) {
            throw new BadRequestException(DEVICE_NAME_REQUIRED);
        }

        if (isBlank(request.deviceType())) {
            throw new BadRequestException(DEVICE_TYPE_REQUIRED);
        }

        if (isBlank(request.clusterId())) {
            throw new BadRequestException(DEVICE_CLUSTER_REQUIRED);
        }
    }

    public static void validateClusterCreateRequest(ClusterCreateRequest request) {
        if (request.clusterName() == null ) {
            throw new BadRequestException(CLUSTER_REQUEST_INVALID);
        }

        if (isBlank(request.clusterName())) {
            throw new BadRequestException(CLUSTER_NAME_REQUIRED);
        }
    }

    public static void validateClusterUpdateRequest(ClusterUpdateRequest request) {
        if (request == null) {
            throw new BadRequestException(CLUSTER_REQUEST_INVALID);
        }
    }

    // ===== HELPER ===== //
    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
