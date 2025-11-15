package com.agrosentinel.auth.model.dto;

import java.time.LocalDateTime;

public record AppResponse<T>(
        String message,
        T data,
        LocalDateTime timestamp
) {}
