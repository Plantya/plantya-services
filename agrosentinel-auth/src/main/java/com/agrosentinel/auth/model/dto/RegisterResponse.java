package com.agrosentinel.auth.model.dto;

import java.time.LocalDateTime;

public record RegisterResponse(
        String username,
        String email,
        String role,
        LocalDateTime createdAt
) {}
