package io.plantya.iot.device.dto.response;

import io.plantya.iot.device.domain.DeviceStatus;

public record DeviceListResponse(
        String deviceId,
        String deviceName,
        String deviceType,
        DeviceStatus status
) {}
