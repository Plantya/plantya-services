package io.plantya.user.common.exception;

public class ConflictException extends ApiException {
    public ConflictException(ApiError error) {
        super(error);
    }
}