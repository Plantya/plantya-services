package io.plantya.management.dto.request;

import io.plantya.management.entity.UserRole;

public record CreateUserRequest(
        String email,
        String name,
        UserRole role,
        String password
) {}
