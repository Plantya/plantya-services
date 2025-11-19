package com.agrosentinel.auth.dto.response;

import java.time.LocalDateTime;

public record RegisterResponse(
        String username,
        String email,
        String role,
        LocalDateTime createdAt
) {}
