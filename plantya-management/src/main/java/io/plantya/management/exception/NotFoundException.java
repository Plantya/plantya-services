package io.plantya.management.exception;

import io.plantya.management.enums.ApiError;

public class NotFoundException extends ApiException {
    public NotFoundException(ApiError error) {
        super(error);
    }

    public NotFoundException(ApiError error, String detail) {
        super(error, detail);
    }
}
