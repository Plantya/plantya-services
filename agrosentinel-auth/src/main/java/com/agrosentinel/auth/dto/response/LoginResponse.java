package com.agrosentinel.auth.dto.response;

public record LoginResponse(
        String username,
        String email,
        String role
) {}
