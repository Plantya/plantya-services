package io.plantya.iot.device.dto.response;

import io.plantya.iot.device.domain.DeviceStatus;

import java.time.Instant;

public record DeviceCreateResponse(
        String deviceId,
        String deviceName,
        String deviceType,
        String clusterId,
        DeviceStatus status,
        Instant createdAt
) {}
