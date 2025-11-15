package com.agrosentinel.auth.model.dto;

public record RegisterRequest(
        String username,
        String email,
        String password,
        String confirmPassword
) {}
