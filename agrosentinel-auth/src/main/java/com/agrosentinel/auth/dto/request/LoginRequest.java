package com.agrosentinel.auth.dto.request;

public record LoginRequest(
        String usernameOrEmail,
        String password
) {}
