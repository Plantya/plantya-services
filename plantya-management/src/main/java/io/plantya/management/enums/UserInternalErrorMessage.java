package io.plantya.management.enums;

public enum UserInternalErrorMessage {

    USER_INTERNAL_ERROR("internal server error occurred"),
    USER_DATABASE_ERROR("database error occurred");

    private final String message;

    UserInternalErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
