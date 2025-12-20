package io.plantya.management.dto.request;

import io.plantya.management.enums.UserRole;

public record UserRequest(
        String email,
        String name,
        UserRole role
) {}
