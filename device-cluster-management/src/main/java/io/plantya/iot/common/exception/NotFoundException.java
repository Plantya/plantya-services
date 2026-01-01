package io.plantya.iot.common.exception;

public class NotFoundException extends ApiException {
    public NotFoundException(ApiError error) {
        super(error);
    }
}