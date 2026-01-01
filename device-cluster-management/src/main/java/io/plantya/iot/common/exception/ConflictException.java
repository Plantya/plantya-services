package io.plantya.iot.common.exception;

public class ConflictException extends ApiException {
    public ConflictException(ApiError error) {
        super(error);
    }
}