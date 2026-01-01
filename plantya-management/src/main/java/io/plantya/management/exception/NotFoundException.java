package io.plantya.management.exception;

public class NotFoundException extends ApiException {
    public NotFoundException(ApiError error) {
        super(error);
    }

    public NotFoundException(ApiError error, String detail) {
        super(error, detail);
    }
}
