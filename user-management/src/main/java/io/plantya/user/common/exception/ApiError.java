package io.plantya.user.common.exception;

public interface ApiError {
    String getCode();
    String getDefaultDetail();
}