package io.plantya.iot.common.mapper;

import io.plantya.iot.cluster.dto.response.ClusterCreateResponse;
import io.plantya.iot.cluster.dto.response.ClusterGetResponse;
import io.plantya.iot.cluster.dto.response.ClusterListResponse;
import io.plantya.iot.cluster.dto.response.ClusterUpdateResponse;
import io.plantya.iot.cluster.entity.Cluster;
import io.plantya.iot.device.domain.Device;
import io.plantya.iot.device.dto.response.DeviceListResponse;

import java.util.List;

public class ClusterResponseMapper {

    public static ClusterListResponse toClusterListResponse(Cluster cluster) {
        return new ClusterListResponse(
                cluster.getClusterId(),
                cluster.getClusterName()
        );
    }

    public static ClusterCreateResponse toClusterCreateResponse(Cluster cluster) {
        return new ClusterCreateResponse(
                cluster.getClusterId(),
                cluster.getClusterName(),
                cluster.getCreatedAt()
        );
    }

    public static ClusterUpdateResponse toClusterUpdateResponse(Cluster cluster) {
        return new ClusterUpdateResponse(
                cluster.getClusterId(),
                cluster.getClusterName(),
                cluster.getUpdatedAt()
        );
    }

    public static ClusterGetResponse toClusterGetResponse(Cluster cluster, List<Device> devices) {
        List<DeviceListResponse> deviceListResponses = devices.stream()
                .map(DeviceResponseMapper::toDeviceListResponse)
                .toList();

        return new ClusterGetResponse(
                cluster.getClusterId(),
                cluster.getClusterName(),
                devices.size(),
                deviceListResponses
        );
    }
}
