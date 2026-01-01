package io.plantya.management.common.repository;

import io.plantya.management.enums.UserRole;

import java.util.ArrayList;
import java.util.List;

public final class UserQueryBuilder {

    private UserQueryBuilder() {}

    public static UserQuerySpec active(String search, UserRole role) {
        return build("deletedAt IS NULL", search, role);
    }

    public static UserQuerySpec deleted(String search, UserRole role) {
        return build("deletedAt IS NOT NULL", search, role);
    }

    private static UserQuerySpec build(String base, String search, UserRole role) {
        StringBuilder jpql = new StringBuilder(base);
        List<Object> params = new ArrayList<>();
        int idx = 1;

        if (search != null && !search.isBlank()) {
            jpql.append("""
                AND (
                    LOWER(userId) LIKE ?%d OR
                    LOWER(email) LIKE ?%d OR
                    LOWER(name) LIKE ?%d OR
                    LOWER(CAST(role AS text)) LIKE ?%d
                )
            """.formatted(idx, idx, idx, idx));
            params.add("%" + search.toLowerCase() + "%");
            idx++;
        }

        if (role != null) {
            jpql.append(" AND role = ?").append(idx);
            params.add(role);
        }

        return new UserQuerySpec(jpql.toString(), params.toArray());
    }
}