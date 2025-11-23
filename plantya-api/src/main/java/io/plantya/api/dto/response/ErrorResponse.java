package io.plantya.api.dto.response;

public record ErrorResponse(
        String timestamp,
        int status,
        String path,
        String message
) {}
