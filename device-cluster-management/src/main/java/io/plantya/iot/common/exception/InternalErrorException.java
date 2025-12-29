package io.plantya.iot.common.exception;

public class InternalErrorException extends ApiException {
    public InternalErrorException(ApiError error) {
        super(error);
    }

    public InternalErrorException(ApiError error, String detail) {
        super(error, detail);
    }
}