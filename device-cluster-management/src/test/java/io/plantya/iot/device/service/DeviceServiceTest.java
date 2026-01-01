package io.plantya.iot.device.service;

import static org.junit.jupiter.api.Assertions.*;

import io.plantya.iot.cluster.entity.Cluster;
import io.plantya.iot.cluster.repository.ClusterRepository;
import io.plantya.iot.cluster.service.ClusterService;
import io.plantya.iot.common.exception.*;
import io.plantya.iot.common.exception.message.ErrorMessage;
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

    @InjectMocks
    DeviceService deviceService;

    @Mock
    DeviceRepository deviceRepository;

    @Mock
    ClusterRepository clusterRepository;

    // =========================================================
    // CREATE DEVICE
    // =========================================================

    @Nested
    @DisplayName("Create Device")
    class CreateDeviceTest {

        private DeviceCreateRequest validRequest() {
            return new DeviceCreateRequest(
                    "Temperature Sensor",
                    "SENSOR",
                    "CL-001"
            );
        }

        private Device createSavedDevice() {
            Device device = new Device();
            device.setDeviceId("DVC-001");
            device.setDeviceName("Temperature Sensor");
            device.setDeviceType("SENSOR");
            device.setClusterId("CL-001");
            device.setStatus(DeviceStatus.OFFLINE);
            device.setCreatedAt(Instant.now());
            return device;
        }

        @Test
        @DisplayName("SUCCESS: Create device successfully")
        void createDevice_success() {
            // Arrange
            DeviceCreateRequest request = validRequest();

            when(clusterRepository.findByClusterId("CL-001"))
                    .thenReturn(Optional.of(new Cluster()));

            when(deviceRepository.save(any(Device.class)))
                    .thenReturn(createSavedDevice());

            // Act
            DeviceCreateResponse response = deviceService.createDevice(request);

            // Assert
            assertNotNull(response);
            assertEquals("DVC-001", response.deviceId());
            assertEquals("Temperature Sensor", response.deviceName());
            assertEquals("SENSOR", response.deviceType());
            assertEquals("CL-001", response.clusterId());
            assertEquals(DeviceStatus.OFFLINE, response.status());
            assertNotNull(response.createdAt());

            verify(clusterRepository, times(1)).findByClusterId("CL-001");
            verify(deviceRepository, times(1)).save(any(Device.class));
        }

        @Test
        @DisplayName("FAIL: Cluster not found")
        void createDevice_clusterNotFound() {
            // Arrange
            DeviceCreateRequest request = validRequest();

            when(clusterRepository.findByClusterId("CL-001"))
                    .thenReturn(Optional.empty());

            // Act & Assert
            NotFoundException exception = assertThrows(
                    NotFoundException.class,
                    () -> deviceService.createDevice(request)
            );

            assertEquals("CLUSTER_NOT_FOUND", exception.getError().getCode());
            verify(deviceRepository, never()).save(any());
        }

        @Test
        @DisplayName("FAIL: Request is null")
        void createDevice_nullRequest() {
            // Act & Assert
            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> deviceService.createDevice(null)
            );

            assertEquals("DEVICE_REQUEST_INVALID", exception.getError().getCode());
        }

        @Test
        @DisplayName("FAIL: Missing device name")
        void createDevice_missingDeviceName() {
            DeviceCreateRequest request =
                    new DeviceCreateRequest(null, "SENSOR", "CL-001");

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> deviceService.createDevice(request)
            );

            assertEquals("DEVICE_NAME_REQUIRED", exception.getError().getCode());
        }

        @Test
        @DisplayName("FAIL: Missing device type")
        void createDevice_missingDeviceType() {
            DeviceCreateRequest request =
                    new DeviceCreateRequest("Sensor", null, "CL-001");

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> deviceService.createDevice(request)
            );

            assertEquals("DEVICE_TYPE_REQUIRED", exception.getError().getCode());
        }

        @Test
        @DisplayName("FAIL: Missing cluster id")
        void createDevice_missingClusterId() {
            DeviceCreateRequest request =
                    new DeviceCreateRequest("Sensor", "SENSOR", null);

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> deviceService.createDevice(request)
            );

            assertEquals("DEVICE_CLUSTER_REQUIRED", exception.getError().getCode());
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

            assertEquals(ErrorMessage.DEVICE_NOT_FOUND, ex.getError());
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

            assertEquals(ErrorMessage.DEVICE_UPDATE_EMPTY, ex.getError());
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

            assertEquals(ErrorMessage.DEVICE_ALREADY_DELETED, ex.getError());
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

            assertEquals(ErrorMessage.DEVICE_NOT_FOUND, ex.getError());
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

            assertEquals(ErrorMessage.DEVICE_ALREADY_DELETED, ex.getError());
        }
    }
}
