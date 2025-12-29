package io.plantya.iot.common.validator;

import io.plantya.iot.common.exception.BadRequestException;
import io.plantya.iot.device.dto.request.DeviceCreateRequest;

import static io.plantya.iot.common.exception.message.DeviceError.*;

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

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
