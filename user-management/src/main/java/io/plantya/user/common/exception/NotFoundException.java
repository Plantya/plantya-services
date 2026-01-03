package io.plantya.user.common.exception;

public class NotFoundException extends ApiException {
    public NotFoundException(ApiError error) {
        super(error);
    }
}