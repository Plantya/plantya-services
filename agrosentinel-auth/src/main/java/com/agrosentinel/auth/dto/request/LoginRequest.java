package com.agrosentinel.auth.dto.request;

public record LoginRequest(
        String username,
        String password
) {}
