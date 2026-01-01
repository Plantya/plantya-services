package io.plantya.iot.common.exception;

public class BadRequestException extends ApiException {
    public BadRequestException(ApiError error) {
        super(error);
    }
}