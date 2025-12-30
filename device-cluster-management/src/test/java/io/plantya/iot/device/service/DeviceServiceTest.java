package io.plantya.iot.device.service;

import static org.junit.jupiter.api.Assertions.*;

import io.plantya.iot.common.exception.*;
import io.plantya.iot.common.exception.message.DeviceError;
import io.plantya.iot.device.domain.Device;
import io.plantya.iot.device.domain.DeviceStatus;
import io.plantya.iot.device.dto.request.DeviceCreateRequest;
import io.plantya.iot.device.dto.request.DeviceUpdateRequest;
import io.plantya.iot.device.dto.response.DeviceCreateResponse;
import io.plantya.iot.device.dto.response.DeviceGetResponse;
import io.plantya.iot.device.dto.response.DeviceUpdateResponse;
import io.plantya.iot.device.repository.DeviceRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.Mockito.*;

@DisplayName("DeviceService Unit Test")
@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    @Mock
    DeviceRepository deviceRepository;

    @InjectMocks
    DeviceService deviceService;

    // =========================================================
    // CREATE DEVICE
    // =========================================================

    @Nested
    @DisplayName("Create Device")
    class CreateDeviceTest {

        @Test
        @DisplayName("Success - valid request")
        void createDevice_success() {
            DeviceCreateRequest request =
                    new DeviceCreateRequest("dev-1", "sensor", "cluster-1");

            Device saved = new Device();
            saved.setDeviceId("D1");
            saved.setDeviceName("dev-1");
            saved.setDeviceType("sensor");
            saved.setClusterId("cluster-1");
            saved.setStatus(DeviceStatus.OFFLINE);

            when(deviceRepository.save(any(Device.class))).thenReturn(saved);

            DeviceCreateResponse response = deviceService.createDevice(request);

            assertEquals("D1", response.deviceId());
            assertEquals("dev-1", response.deviceName());
            assertEquals(DeviceStatus.OFFLINE, response.status());
        }

        @Test
        @DisplayName("Fail - device name missing")
        void createDevice_missingName() {
            DeviceCreateRequest request =
                    new DeviceCreateRequest(null, "type", "cluster");

            BadRequestException ex =
                    assertThrows(BadRequestException.class,
                            () -> deviceService.createDevice(request));

            assertEquals(DeviceError.DEVICE_NAME_REQUIRED, ex.getError());
        }

        @Test
        @DisplayName("Fail - device type missing")
        void createDevice_missingType() {
            DeviceCreateRequest request =
                    new DeviceCreateRequest("dev", null, "cluster");

            BadRequestException ex =
                    assertThrows(BadRequestException.class,
                            () -> deviceService.createDevice(request));

            assertEquals(DeviceError.DEVICE_TYPE_REQUIRED, ex.getError());
        }

        @Test
        @DisplayName("Fail - cluster id missing")
        void createDevice_missingCluster() {
            DeviceCreateRequest request =
                    new DeviceCreateRequest("dev", "type", null);

            BadRequestException ex =
                    assertThrows(BadRequestException.class,
                            () -> deviceService.createDevice(request));

            assertEquals(DeviceError.DEVICE_CLUSTER_REQUIRED, ex.getError());
        }
    }

    // =========================================================
    // FIND BY ID
    // =========================================================

    @Nested
    @DisplayName("Find Device By ID")
    class FindDevice {

        @Test
        @DisplayName("Success - device found")
        void findDevice_success() {
            Device device = new Device();
            device.setDeviceId("D1");
            device.setDeviceName("device");
            device.setDeviceType("sensor");
            device.setClusterId("cluster");
            device.setStatus(DeviceStatus.ONLINE);

            when(deviceRepository.findByDeviceId("D1"))
                    .thenReturn(Optional.of(device));

            DeviceGetResponse response = deviceService.findDeviceByDeviceId("D1");

            assertEquals("D1", response.deviceId());
            assertEquals(DeviceStatus.ONLINE, response.status());
        }

        @Test
        @DisplayName("Fail - device not found")
        void findDevice_notFound() {
            when(deviceRepository.findByDeviceId("X"))
                    .thenReturn(Optional.empty());

            NotFoundException ex =
                    assertThrows(NotFoundException.class,
                            () -> deviceService.findDeviceByDeviceId("X"));

            assertEquals(DeviceError.DEVICE_NOT_FOUND, ex.getError());
        }
    }

    // =========================================================
    // UPDATE DEVICE
    // =========================================================

    @Nested
    @DisplayName("Update Device")
    class UpdateDevice {

        @Test
        @DisplayName("Success - partial update")
        void updateDevice_partial() {
            Device device = new Device();
            device.setDeviceId("D1");
            device.setDeviceName("old");
            device.setDeviceType("oldType");
            device.setStatus(DeviceStatus.OFFLINE);

            when(deviceRepository.findByDeviceId("D1"))
                    .thenReturn(Optional.of(device));

            DeviceUpdateRequest req =
                    new DeviceUpdateRequest("newName", null, DeviceStatus.ONLINE);

            DeviceUpdateResponse response =
                    deviceService.updateDevice("D1", req);

            assertEquals("newName", response.deviceName());
            assertEquals(DeviceStatus.ONLINE, response.status());
            assertEquals("oldType", response.deviceType());
        }

        @Test
        @DisplayName("Fail - no fields to update")
        void updateDevice_emptyRequest() {
            Device device = new Device();
            when(deviceRepository.findByDeviceId("D1"))
                    .thenReturn(Optional.of(device));

            DeviceUpdateRequest request =
                    new DeviceUpdateRequest(null, null, null);

            BadRequestException ex =
                    assertThrows(BadRequestException.class,
                            () -> deviceService.updateDevice("D1", request));

            assertEquals(DeviceError.DEVICE_UPDATE_EMPTY, ex.getError());
        }

        @Test
        @DisplayName("Fail - already deleted")
        void updateDevice_deleted() {
            Device device = new Device();
            device.setDeletedAt(Instant.now());

            when(deviceRepository.findByDeviceId("D1"))
                    .thenReturn(Optional.of(device));

            ConflictException ex =
                    assertThrows(ConflictException.class,
                            () -> deviceService.updateDevice(
                                    "D1",
                                    new DeviceUpdateRequest("x", null, null)));

            assertEquals(DeviceError.DEVICE_ALREADY_DELETED, ex.getError());
        }
    }

    // =========================================================
    // DELETE
    // =========================================================

    @Nested
    @DisplayName("Delete Device")
    class DeleteDevice {

        @Test
        @DisplayName("Success")
        void deleteDevice_success() {
            Device device = new Device();
            when(deviceRepository.findByDeviceId("D1"))
                    .thenReturn(Optional.of(device));

            deviceService.deleteDevice("D1");

            verify(deviceRepository).softDelete("D1");
        }

        @Test
        @DisplayName("Fail - not found")
        void deleteDevice_notFound() {
            when(deviceRepository.findByDeviceId("X"))
                    .thenReturn(Optional.empty());

            NotFoundException ex =
                    assertThrows(NotFoundException.class,
                            () -> deviceService.deleteDevice("X"));

            assertEquals(DeviceError.DEVICE_NOT_FOUND, ex.getError());
        }

        @Test
        @DisplayName("Fail - already deleted")
        void deleteDevice_alreadyDeleted() {
            Device device = new Device();
            device.setDeletedAt(Instant.now());

            when(deviceRepository.findByDeviceId("D1"))
                    .thenReturn(Optional.of(device));

            ConflictException ex =
                    assertThrows(ConflictException.class,
                            () -> deviceService.deleteDevice("D1"));

            assertEquals(DeviceError.DEVICE_ALREADY_DELETED, ex.getError());
        }
    }
}
