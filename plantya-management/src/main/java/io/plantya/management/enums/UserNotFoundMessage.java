package io.plantya.management.enums;

public enum UserNotFoundMessage implements ApiError {

    USER_NOT_FOUND("USER_NOT_FOUND", "user not found"),
    USER_DELETED_NOT_FOUND("USER_DELETED_NOT_FOUND", "deleted user not found");

    private final String code;
    private final String defaultDetail;

    UserNotFoundMessage(String code, String defaultDetail) {
        this.code = code;
        this.defaultDetail = defaultDetail;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDefaultDetail() {
        return defaultDetail;
    }

}
