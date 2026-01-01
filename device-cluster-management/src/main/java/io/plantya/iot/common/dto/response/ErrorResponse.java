package io.plantya.iot.common.dto.response;

import java.time.Instant;

public record ErrorResponse(
        String title,
        int status,
        String detail,
        String instance,
        String code,
        Instant timestamp
) {}