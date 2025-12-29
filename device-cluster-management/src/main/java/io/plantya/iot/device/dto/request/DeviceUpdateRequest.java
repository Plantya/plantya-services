package io.plantya.iot.device.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.plantya.iot.device.domain.DeviceStatus;

public record DeviceUpdateRequest(
        @JsonProperty("device_name") String deviceName,
        @JsonProperty("device_type") String deviceType,
        @JsonProperty("status") DeviceStatus status
) {}