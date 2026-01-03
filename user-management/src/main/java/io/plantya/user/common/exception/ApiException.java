package io.plantya.user.common.exception;

public abstract class ApiException extends RuntimeException {

    private final ApiError error;
    private final String detail;

    protected ApiException(ApiError error) {
        super(error.getDefaultDetail());
        this.error = error;
        this.detail = error.getDefaultDetail();
    }

    public ApiError getError() {
        return error;
    }

    public String getDetail() {
        return detail;
    }
}