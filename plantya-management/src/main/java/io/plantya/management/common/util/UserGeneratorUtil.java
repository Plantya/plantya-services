package io.plantya.management.common.util;

import io.plantya.management.enums.UserRole;

public class UserGeneratorUtil {

    public UserGeneratorUtil() {}

    public static String generateUserId(UserRole role, long lastIndex) {
        char prefix = switch (role) {
            case USER -> 'U';
            case STAFF -> 'S';
            case ADMIN -> 'A';
        };

        return prefix + String.format("%05d", lastIndex);
    }

    public static String generateDefaultPassword(String userId) {
        return "plantya_" + userId;
    }

}
