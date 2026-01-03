package io.plantya.user.common.exception;

public class InternalErrorException extends ApiException {
    public InternalErrorException(ApiError error) {
        super(error);
    }
}