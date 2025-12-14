package io.plantya.management.enums;

public enum UserConflictMessage {

    USER_EMAIL_ALREADY_EXISTS("email is already in use"),
    USER_ALREADY_DELETED("user is already deleted"),
    USER_ALREADY_ACTIVE("user is already active");

    private final String message;

    UserConflictMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}