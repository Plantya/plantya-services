package io.plantya.auth.dto.request;

public record RegisterRequest(
        String username,
        String email,
        String password,
        String confirmPassword
) {}
