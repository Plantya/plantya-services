package com.agrosentinel.auth.model.dto;

public record LoginRequest(
        String username,
        String password
) {}
