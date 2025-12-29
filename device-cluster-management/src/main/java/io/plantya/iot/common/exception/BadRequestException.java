package io.plantya.iot.common.exception;

public class BadRequestException extends ApiException {
    public BadRequestException(ApiError error) {
        super(error);
    }

    public BadRequestException(ApiError error, String detail) {
        super(error, detail);
    }
}