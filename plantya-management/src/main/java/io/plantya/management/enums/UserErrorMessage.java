package io.plantya.management.enums;

import io.plantya.management.exception.ApiError;

public enum UserErrorMessage implements ApiError {

    USER_FIELD_REQUIRED(
            "USER_FIELD_REQUIRED",
            "required field is missing"
    ),

    USER_INVALID_REQUEST_PAYLOAD(
            "USER_INVALID_REQUEST_PAYLOAD",
            "invalid request payload"
    ),

    USER_INVALID_ID_FORMAT(
            "USER_INVALID_ID_FORMAT",
            "invalid user id format"
    ),

    USER_INVALID_EMAIL_FORMAT(
            "USER_INVALID_EMAIL_FORMAT",
            "invalid email format"
    ),

    USER_INVALID_PASSWORD(
            "USER_INVALID_PASSWORD",
            "invalid password format"
    ),

    USER_INVALID_NAME(
            "USER_INVALID_NAME",
            "invalid user name"
    ),

    USER_INVALID_ROLE(
            "USER_INVALID_ROLE",
            "invalid user role"
    ),

    USER_PATCH_EMPTY(
            "USER_PATCH_EMPTY",
            "at least one field must be provided"
    ),

    USER_NOT_FOUND(
            "USER_NOT_FOUND",
                    "user not found"
    ),

    USER_DELETED_NOT_FOUND(
            "USER_DELETED_NOT_FOUND",
                    "deleted user not found"
    ),

    USER_EMAIL_ALREADY_EXISTS(
            "USER_EMAIL_ALREADY_EXISTS",
            "email is already in use"
    ),

    USER_ALREADY_DELETED(
            "USER_ALREADY_DELETED",
            "user is already deleted"
    ),

    USER_ALREADY_ACTIVE(
            "USER_ALREADY_ACTIVE",
            "user is already active"
    ),

    USER_INTERNAL_ERROR(
            "USER_INTERNAL_ERROR",
            "internal server error occurred"
    ),

    USER_DATABASE_ERROR(
            "USER_DATABASE_ERROR",
            "database error occurred"
    );

    private final String code;
    private final String defaultDetail;

    UserErrorMessage(String code, String defaultDetail) {
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
