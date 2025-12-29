package io.plantya.iot.device.dto.request;

import io.plantya.iot.device.domain.DeviceStatus;

public record DeviceUpdateRequest(
        String deviceName,
        String deviceType,
        DeviceStatus status
) {}