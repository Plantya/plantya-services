package io.plantya.user.dto.response;

import io.plantya.user.domain.UserRole;

import java.time.Instant;

public record UserCreateResponse(
        String email,
        String name,
        UserRole role,
        Instant createdAt
) {}
