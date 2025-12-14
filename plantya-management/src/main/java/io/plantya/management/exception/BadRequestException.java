package io.plantya.management.exception;

import io.plantya.management.enums.ApiError;

public class BadRequestException extends ApiException {
    public BadRequestException(ApiError error) {
        super(error);
    }

    public BadRequestException(ApiError error, String detail) {
        super(error, detail);
    }
}
