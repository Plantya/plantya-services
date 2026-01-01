package io.plantya.iot.device.service;

import io.plantya.iot.cluster.repository.ClusterRepository;
import io.plantya.iot.common.dto.param.DeviceParam;
import io.plantya.iot.common.exception.BadRequestException;
import io.plantya.iot.common.exception.ConflictException;
import io.plantya.iot.common.exception.NotFoundException;
import io.plantya.iot.common.mapper.DeviceResponseMapper;
import io.plantya.iot.common.validator.RequestValidator;
import io.plantya.iot.device.domain.Device;
import io.plantya.iot.device.domain.DeviceStatus;
import io.plantya.iot.device.dto.request.DeviceCreateRequest;
import io.plantya.iot.device.dto.request.DeviceUpdateRequest;
import io.plantya.iot.device.dto.response.DeviceCreateResponse;
import io.plantya.iot.device.dto.response.DeviceGetResponse;
import io.plantya.iot.device.dto.response.DeviceUpdateResponse;
import io.plantya.iot.device.dto.response.PagedDeviceResponse;
import io.plantya.iot.device.repository.DeviceRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.List;

import static io.plantya.iot.common.exception.message.ErrorMessage.*;

@ApplicationScoped
public class DeviceService {

    @Inject
    DeviceRepository deviceRepository;

    @Inject
    ClusterRepository clusterRepository;

    private final Logger LOG = Logger.getLogger(DeviceService.class);

    public PagedDeviceResponse findAllExistingDevices(DeviceParam param) {
        if (param.page() < 1) {
            throw new BadRequestException(PAGE_LOWER_THAN_ONE);
        }

        List<Device> devices = deviceRepository.findAllExistingDevices(param);
        long totalData = deviceRepository.countExistingDevices(param);

        List<DeviceGetResponse> responses = devices.stream()
                .map(DeviceResponseMapper::toDeviceGetResponse)
                .toList();

        int totalPages = (int) Math.ceil((double) totalData / param.size());

        return new PagedDeviceResponse(
                responses.size(),
                param.page(),
                param.size(),
                totalPages,
                responses
        );
    }

    @Transactional
    public DeviceCreateResponse createDevice(DeviceCreateRequest request) {
        RequestValidator.validateDeviceCreateRequest(request);

        LOG.infof(
                "Creating device: deviceName=%s, deviceType=%s, clusterId=%s",
                request.deviceName(), request.deviceType(), request.clusterId()
        );

        Device device = new Device();
        device.setDeviceName(request.deviceName());
        device.setDeviceType(request.deviceType());
        device.setClusterId(request.clusterId());
        device.setStatus(DeviceStatus.OFFLINE);

        boolean isClusterPresent = clusterRepository.findByClusterId(request.clusterId()).isPresent();
        if (!isClusterPresent) {
            LOG.errorf("Cluster not found: clusterId=%s", request.clusterId());
            throw new NotFoundException(CLUSTER_NOT_FOUND);
        }

        Device savedDevice = deviceRepository.save(device);

        LOG.infof("Device created successfully: deviceId=%s", savedDevice.getDeviceId());
        return DeviceResponseMapper.toDeviceCreateResponse(savedDevice);
    }

    public DeviceGetResponse findDeviceByDeviceId(String deviceId) {
        Device device = deviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new NotFoundException(DEVICE_NOT_FOUND));

        return DeviceResponseMapper.toDeviceGetResponse(device);
    }

    @Transactional
    public DeviceUpdateResponse updateDevice(String deviceId, DeviceUpdateRequest request) {
        LOG.infof("Patch device: deviceId=%s", deviceId);

        Device device = deviceRepository.findByDeviceId(deviceId).orElseThrow(() -> {
            LOG.errorf("Device not found: deviceId=%s", deviceId);
            return new NotFoundException(DEVICE_NOT_FOUND);
        });

        if (device.getDeletedAt() != null) {
            LOG.errorf("Device already deleted: deviceId=%s", deviceId);
            throw new ConflictException(DEVICE_ALREADY_DELETED);
        }

        boolean isUpdated = false;

        if (request.deviceName() != null) {
            device.setDeviceName(request.deviceName());
            isUpdated = true;
        }

        if (request.deviceType() != null) {
            device.setDeviceType(request.deviceType());
            isUpdated = true;
        }

        if (request.status() != null) {
            device.setStatus(request.status());
            isUpdated = true;
        }

        if (!isUpdated) {
            LOG.warnf("Patch device ignored - no fields provided: deviceId=%s", deviceId);
            throw new BadRequestException(DEVICE_UPDATE_EMPTY);
        }

        LOG.debugf(
                "Patch fields: deviceName=%s, deviceType=%s, status=%s",
                request.deviceName(),
                request.deviceType(),
                request.status()
        );

        device.setUpdatedAt(Instant.now());

        LOG.infof("Device patched successfully: deviceId=%s", deviceId);
        return DeviceResponseMapper.toDeviceUpdateResponse(device);
    }

    @Transactional
    public void deleteDevice(String deviceId) {
        LOG.infof("Delete device request: deviceId=%s", deviceId);

        Device device = deviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new NotFoundException(DEVICE_NOT_FOUND));

        if (device.getDeletedAt() != null) {
            LOG.warnf("Delete device failed - already deleted: deviceId=%s", deviceId);
            throw new ConflictException(DEVICE_ALREADY_DELETED);
        }

        deviceRepository.softDelete(deviceId);

        LOG.infof("Device deleted successfully: deviceId=%s", deviceId);
    }
}
