package io.plantya.management.enums;

import io.plantya.management.exception.ApiError;

public enum UserBadRequestMessage implements ApiError {

    USER_ORDER_INVALID(
            "USER_ORDER_INVALID",
            "invalid order value"
    ),

    USER_PAGINATION_PARAMETER_INCOMPLETE(
            "USER_PAGINATION_PARAMETER_INCOMPLETE",
            "page and size query parameters must be specified together"
    ),
    USER_PAGINATION_PARAMETER_INVALID(
            "USER_PAGINATION_PARAMETER_INVALID",
            "page and size must be greater than or equal to 1"
    ),

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
            "email format is invalid"
    ),
    USER_INVALID_PASSWORD(
            "USER_INVALID_PASSWORD",
            "password format is invalid"
    ),
    USER_INVALID_NAME(
            "USER_INVALID_NAME",
            "user name is invalid"
    ),
    USER_INVALID_ROLE(
            "USER_INVALID_ROLE",
            "invalid role value"
    ),

    USER_PATCH_EMPTY(
            "USER_PATCH_EMPTY",
            "at least one field must be provided"
    );

    private final String code;
    private final String defaultDetail;

    UserBadRequestMessage(String code, String defaultDetail) {
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