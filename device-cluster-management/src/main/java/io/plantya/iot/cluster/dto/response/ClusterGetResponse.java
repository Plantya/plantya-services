package io.plantya.iot.cluster.dto.response;

import io.plantya.iot.device.dto.response.DeviceListResponse;

import java.util.List;

public record ClusterGetResponse(
        String clusterId,
        String clusterName,
        long totalDevices,
        List<DeviceListResponse> devices
) {}
