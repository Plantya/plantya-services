package io.plantya.management.common.repository;

public final class UserSortResolver {

    private UserSortResolver() {}

    public static String resolveField(String sort) {
        if (sort == null || sort.isBlank()) {
            return "userId";
        }

        return switch (sort) {
            case "email", "name", "role", "createdAt", "userId" -> sort;
            default -> "userId";
        };
    }

    public static String resolveOrder(String order) {
        return "DESC".equalsIgnoreCase(order) ? "DESC" : "ASC";
    }

    public static String orderBy(String sort, String order) {
        return " ORDER BY " + resolveField(sort) + " " + resolveOrder(order);
    }
}
