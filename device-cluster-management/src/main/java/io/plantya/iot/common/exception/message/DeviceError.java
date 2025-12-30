package io.plantya.iot.common.exception.message;

import io.plantya.iot.common.exception.ApiError;

public enum DeviceError implements ApiError {

    PAGE_LOWER_THAN_ONE("PAGE_LOWER_THAN_ONE", "page number must be greater than 0"),

    DEVICE_NOT_FOUND("DEVICE_NOT_FOUND", "device not found"),
    DEVICE_ALREADY_DELETED("DEVICE_ALREADY_DELETED", "device already deleted"),
    DEVICE_UPDATE_EMPTY("DEVICE_UPDATE_EMPTY", "no fields provided to update"),

    DEVICE_REQUEST_INVALID("DEVICE_REQUEST_INVALID", "invalid device request"),
    DEVICE_NAME_REQUIRED("DEVICE_NAME_REQUIRED", "device name is required"),
    DEVICE_TYPE_REQUIRED("DEVICE_TYPE_REQUIRED", "device type is required"),
    DEVICE_CLUSTER_REQUIRED("DEVICE_CLUSTER_REQUIRED", "cluster id is required");

    private final String code;
    private final String defaultDetail;

    DeviceError(String code, String defaultDetail) {
        this.code = code;
        this.defaultDetail = defaultDetail;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDefaultDetail() {
        return defaultDetail;
    }
}