package io.plantya.iot.common.dto.param;

import io.plantya.iot.device.domain.DeviceStatus;

public record DeviceParam(
        int page,
        int size,
        String search,
        String sort,
        String order,
        DeviceStatus status
) {}
