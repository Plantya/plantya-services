package io.plantya.iot.device.dto.request;

public record DeviceCreateRequest(
        String deviceName,
        String deviceType,
        String clusterId
) {}
