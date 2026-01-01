package io.plantya.iot.common.exception;

public interface ApiError {
    String getCode();
    String getDefaultDetail();
}