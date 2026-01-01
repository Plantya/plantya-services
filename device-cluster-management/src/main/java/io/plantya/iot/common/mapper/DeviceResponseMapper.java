package io.plantya.iot.common.mapper;

import io.plantya.iot.device.domain.Device;
import io.plantya.iot.device.dto.response.DeviceCreateResponse;
import io.plantya.iot.device.dto.response.DeviceGetResponse;
import io.plantya.iot.device.dto.response.DeviceListResponse;
import io.plantya.iot.device.dto.response.DeviceUpdateResponse;

public class DeviceResponseMapper {

    public static DeviceGetResponse toDeviceGetResponse(Device device) {
        return new DeviceGetResponse(
                device.getDeviceId(),
                device.getDeviceName(),
                device.getDeviceType(),
                device.getClusterId(),
                device.getStatus(),
                device.getCreatedAt(),
                device.getUpdatedAt()
        );
    }

    public static DeviceCreateResponse toDeviceCreateResponse(Device device) {
        return new DeviceCreateResponse(
                device.getDeviceId(),
                device.getDeviceName(),
                device.getDeviceType(),
                device.getClusterId(),
                device.getStatus(),
                device.getCreatedAt()
        );
    }

    public static DeviceUpdateResponse toDeviceUpdateResponse(Device device) {
        return new DeviceUpdateResponse(
                device.getDeviceId(),
                device.getDeviceName(),
                device.getDeviceType(),
                device.getStatus(),
                device.getClusterId(),
                device.getCreatedAt()
        );
    }

    public static DeviceListResponse toDeviceListResponse(Device device) {
        return new DeviceListResponse(
                device.getDeviceId(),
                device.getDeviceName(),
                device.getDeviceType(),
                device.getStatus()
        );
    }
}
