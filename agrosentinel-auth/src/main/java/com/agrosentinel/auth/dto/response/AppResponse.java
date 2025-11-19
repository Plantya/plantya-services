package com.agrosentinel.auth.dto.response;

import java.time.LocalDateTime;

public record AppResponse<T>(
        String message,
        T data,
        LocalDateTime timestamp
) {}
