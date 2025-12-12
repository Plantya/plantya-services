package io.plantya.management.dto;

import io.plantya.management.entity.UserRole;

import java.time.Instant;

public record DeletedUserDto(
        String userId,
        String email,
        String name,
        UserRole role,
        Instant deletedAt
) {
}
