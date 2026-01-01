package io.plantya.iot.common.exception.message;

import io.plantya.iot.common.exception.ApiError;

public enum ErrorMessage implements ApiError {

    PAGE_LOWER_THAN_ONE("PAGE_LOWER_THAN_ONE", "page number must be greater than 0"),

    CLUSTER_REQUEST_INVALID("CLUSTER_REQUEST_INVALID", "invalid cluster request"),
    CLUSTER_NAME_REQUIRED("CLUSTER_NAME_REQUIRED", "cluster name is required"),
    CLUSTER_ALREADY_EXISTS("CLUSTER_ALREADY_EXISTS", "cluster already exists"),
    CLUSTER_ALREADY_DELETED("CLUSTER_ALREADY_DELETED", "cluster already deleted"),
    CLUSTER_NOT_FOUND("CLUSTER_NOT_FOUND", "cluster not found"),
    CLUSTER_UPDATE_EMPTY("CLUSTER_UPDATE_EMPTY", "no fields provided to update"),

    DEVICE_NOT_FOUND("DEVICE_NOT_FOUND", "device not found"),
    DEVICE_ALREADY_DELETED("DEVICE_ALREADY_DELETED", "device already deleted"),
    DEVICE_UPDATE_EMPTY("DEVICE_UPDATE_EMPTY", "no fields provided to update"),

    DEVICE_REQUEST_INVALID("DEVICE_REQUEST_INVALID", "invalid device request"),
    DEVICE_NAME_REQUIRED("DEVICE_NAME_REQUIRED", "device name is required"),
    DEVICE_TYPE_REQUIRED("DEVICE_TYPE_REQUIRED", "device type is required"),
    DEVICE_CLUSTER_REQUIRED("DEVICE_CLUSTER_REQUIRED", "cluster id is required"),

    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "internal server error");

    private final String code;
    private final String defaultDetail;

    ErrorMessage(String code, String defaultDetail) {
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