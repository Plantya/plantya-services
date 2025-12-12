package io.plantya.management.dto;

import io.plantya.management.entity.UserRole;

import java.time.Instant;

public record GetUserDto(
        String userId,
        String email,
        String name,
        UserRole role,
        Instant createdAt
) {}
