package io.plantya.iot.common.exception;

public abstract class ApiException extends RuntimeException {

    private final ApiError error;
    private final String detail;

    protected ApiException(ApiError error) {
        super(error.getDefaultDetail());
        this.error = error;
        this.detail = error.getDefaultDetail();
    }

    protected ApiException(ApiError error, String detail) {
        super(detail);
        this.error = error;
        this.detail = detail;
    }

    public ApiError getError() {
        return error;
    }

    public String getDetail() {
        return detail;
    }
}