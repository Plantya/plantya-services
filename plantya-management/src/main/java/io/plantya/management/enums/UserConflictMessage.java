package io.plantya.management.enums;

import io.plantya.management.exception.ApiError;

public enum UserConflictMessage implements ApiError {

    USER_EMAIL_ALREADY_EXISTS("USER_EMAIL_ALREADY_EXISTS", "email is already in use"),
    USER_ALREADY_DELETED("USER_ALREADY_DELETED", "user is already deleted"),
    USER_ALREADY_ACTIVE("USER_ALREADY_ACTIVE", "user is already active");

    private final String code;
    private final String defaultDetail;

    UserConflictMessage(String code, String defaultDetail) {
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