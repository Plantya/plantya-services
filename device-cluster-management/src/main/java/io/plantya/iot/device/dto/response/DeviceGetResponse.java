package io.plantya.iot.device.dto.response;

import io.plantya.iot.device.domain.DeviceStatus;

import java.time.Instant;

public record DeviceGetResponse(
        String deviceId,
        String deviceName,
        String deviceType,
        String clusterId,
        DeviceStatus status,
        Instant createdAt,
        Instant updatedAt
) {}
