package io.plantya.auth.dto.response;

import java.time.LocalDateTime;

public record ErrorResponse(
        String path,
        int status,
        String message,
        LocalDateTime timestamp
) {}
