package io.plantya.management.exception;

import io.plantya.management.enums.ApiError;

public class ConflictException extends ApiException {
    public ConflictException(ApiError error) {
        super(error);
    }

    public ConflictException(ApiError error, String detail) {
        super(error, detail);
    }
}
