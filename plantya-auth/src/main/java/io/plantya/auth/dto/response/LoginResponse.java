package io.plantya.auth.dto.response;

public record LoginResponse(
        String username,
        String email,
        String role
) {}
