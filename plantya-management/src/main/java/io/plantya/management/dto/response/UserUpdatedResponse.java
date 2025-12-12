package io.plantya.management.dto.response;

import io.plantya.management.entity.UserRole;

import java.time.Instant;

public record UserUpdatedResponse(
        String userId,
        String email,
        String name,
        UserRole role,
        Instant updatedAt
) {}
