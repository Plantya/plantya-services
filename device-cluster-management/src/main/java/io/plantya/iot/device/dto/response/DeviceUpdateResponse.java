package io.plantya.iot.device.dto.response;

import io.plantya.iot.device.domain.DeviceStatus;

import java.time.Instant;

public record DeviceUpdateResponse(
        String deviceId,
        String deviceName,
        String deviceType,
        DeviceStatus status,
        String clusterId,
        Instant updatedAt
) {}
