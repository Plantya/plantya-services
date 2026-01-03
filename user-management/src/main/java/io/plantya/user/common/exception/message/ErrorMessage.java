package io.plantya.user.common.exception.message;

import io.plantya.user.common.exception.ApiError;

public enum ErrorMessage implements ApiError {
    PAGE_LOWER_THAN_ONE("PAGE_LOWER_THAN_ONE", "page number must be greater than 0"),

    USER_PATCH_EMPTY("USER_PATCH_EMPTY", "at least one field must be provided"),

    USER_NOT_FOUND("USER_NOT_FOUND", "user not found"),
    USER_DELETED_NOT_FOUND("USER_DELETED_NOT_FOUND", "deleted user not found"),

    USER_EMAIL_ALREADY_EXISTS("USER_EMAIL_ALREADY_EXISTS", "email is already in use"),
    USER_ALREADY_DELETED("USER_ALREADY_DELETED", "user is already deleted"),
    USER_ALREADY_ACTIVE("USER_ALREADY_ACTIVE", "user is already active"),

    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "internal server error");

    private final String code;
    private final String defaultDetail;

    ErrorMessage(String code, String defaultDetail) {
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