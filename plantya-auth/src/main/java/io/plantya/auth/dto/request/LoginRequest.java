package io.plantya.auth.dto.request;

public record LoginRequest(
        String usernameOrEmail,
        String password
) {}
