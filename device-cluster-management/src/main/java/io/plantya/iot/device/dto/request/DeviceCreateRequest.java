package io.plantya.iot.device.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DeviceCreateRequest(
        @JsonProperty("device_name") String deviceName,
        @JsonProperty("device_type") String deviceType,
        @JsonProperty("cluster_id") String clusterId
) {}
