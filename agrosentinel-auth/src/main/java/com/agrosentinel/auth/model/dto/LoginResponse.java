package com.agrosentinel.auth.model.dto;

public record LoginResponse(
        String username,
        String email,
        String role
) {}
