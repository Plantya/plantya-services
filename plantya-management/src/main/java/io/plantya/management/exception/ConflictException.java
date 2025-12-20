package io.plantya.management.exception;

public class ConflictException extends ApiException {
    public ConflictException(ApiError error) {
        super(error);
    }

    public ConflictException(ApiError error, String detail) {
        super(error, detail);
    }
}
