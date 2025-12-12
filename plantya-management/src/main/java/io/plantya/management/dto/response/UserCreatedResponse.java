package io.plantya.management.dto.response;

import io.plantya.management.entity.UserRole;

import java.time.Instant;

public record UserCreatedResponse(
        String email,
        String name,
        UserRole role,
        Instant createdAt
) {}
