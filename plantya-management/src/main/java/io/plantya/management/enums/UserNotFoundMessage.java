package io.plantya.management.enums;

public enum UserNotFoundMessage {

    USER_NOT_FOUND("user not found"),
    USER_DELETED_NOT_FOUND("deleted user not found");

    private final String message;

    UserNotFoundMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
