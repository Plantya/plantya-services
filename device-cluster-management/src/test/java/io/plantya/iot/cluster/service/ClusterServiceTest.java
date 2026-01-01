package io.plantya.iot.cluster.service;

import io.plantya.iot.cluster.dto.request.ClusterCreateRequest;
import io.plantya.iot.cluster.dto.request.ClusterUpdateRequest;
import io.plantya.iot.cluster.dto.response.ClusterCreateResponse;
import io.plantya.iot.cluster.dto.response.ClusterGetResponse;
import io.plantya.iot.cluster.dto.response.ClusterUpdateResponse;
import io.plantya.iot.cluster.dto.response.PagedClusterResponse;
import io.plantya.iot.cluster.entity.Cluster;
import io.plantya.iot.cluster.repository.ClusterRepository;
import io.plantya.iot.common.dto.request.ClusterQueryParam;
import io.plantya.iot.common.exception.BadRequestException;
import io.plantya.iot.common.exception.ConflictException;
import io.plantya.iot.common.exception.NotFoundException;
import io.plantya.iot.device.domain.Device;
import io.plantya.iot.device.repository.DeviceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClusterServiceTest {

    @InjectMocks
    private ClusterService clusterService;

    @Mock
    private ClusterRepository clusterRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @Nested
    @DisplayName("Find All Existing Clusters")
    class FindAllExistingClusters {

        @Test
        @DisplayName("SUCCESS: Get paged cluster list")
        void findAllExistingClusters_success() {
            // Arrange
            ClusterQueryParam param = createQueryParam(1, 10);

            List<Cluster> clusters = List.of(
                    createCluster("CL-001", "Cluster A"),
                    createCluster("CL-002", "Cluster B")
            );

            when(clusterRepository.findAllExistingClusters(param))
                    .thenReturn(clusters);
            when(clusterRepository.countExistingDevices(param))
                    .thenReturn(2L);

            // Act
            PagedClusterResponse response = clusterService.findAllExistingClusters(param);

            // Assert
            assertNotNull(response);
            assertEquals(2, response.countData());
            assertEquals(1, response.page());
            assertEquals(10, response.size());
            assertEquals(1, response.totalPages());
            assertEquals(2, response.clusters().size());

            assertEquals("CL-001", response.clusters().getFirst().clusterId());
            assertEquals("Cluster A", response.clusters().getFirst().clusterName());
        }

        @Test
        @DisplayName("FAIL: Page number less than 1")
        void findAllExistingClusters_pageLessThanOne() {
            // Arrange
            ClusterQueryParam param = createQueryParam(0, 10);

            // Act & Assert
            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> clusterService.findAllExistingClusters(param)
            );

            assertEquals("PAGE_LOWER_THAN_ONE", exception.getError().getCode());
        }

        @Test
        @DisplayName("SUCCESS: Empty result returns empty list")
        void findAllExistingClusters_emptyResult() {
            // Arrange
            ClusterQueryParam param = createQueryParam(1, 10);

            when(clusterRepository.findAllExistingClusters(param))
                    .thenReturn(List.of());
            when(clusterRepository.countExistingDevices(param))
                    .thenReturn(0L);

            // Act
            PagedClusterResponse response = clusterService.findAllExistingClusters(param);

            // Assert
            assertNotNull(response);
            assertEquals(0, response.countData());
            assertEquals(0, response.totalPages());
            assertTrue(response.clusters().isEmpty());
        }

        @Test
        @DisplayName("SUCCESS: Correct total pages calculation")
        void findAllExistingClusters_totalPagesCalculation() {
            // Arrange
            ClusterQueryParam param = createQueryParam(2, 3);

            List<Cluster> clusters = List.of(
                    createCluster("CL-1", "A"),
                    createCluster("CL-2", "B"),
                    createCluster("CL-3", "C")
            );

            when(clusterRepository.findAllExistingClusters(param))
                    .thenReturn(clusters);
            when(clusterRepository.countExistingDevices(param))
                    .thenReturn(7L);

            // Act
            PagedClusterResponse response = clusterService.findAllExistingClusters(param);

            // Assert
            assertEquals(3, response.totalPages()); // ceil(7 / 3)
            assertEquals(2, response.page());
            assertEquals(3, response.size());
        }

        private Cluster createCluster(String id, String name) {
            Cluster cluster = new Cluster();
            cluster.setClusterId(id);
            cluster.setClusterName(name);
            return cluster;
        }

        private ClusterQueryParam createQueryParam(int page, int size) {
            ClusterQueryParam param = new ClusterQueryParam();
            param.setPage(page);
            param.setSize(size);
            return param;
        }
    }

    @Nested
    @DisplayName("Create Cluster")
    class CreateCluster {

        @Test
        @DisplayName("Should create cluster successfully when request is valid")
        void shouldCreateClusterSuccessfully() {
            // given
            ClusterCreateRequest request = new ClusterCreateRequest("Test Cluster");

            Cluster savedCluster = new Cluster();
            savedCluster.setClusterName("Test Cluster");
            savedCluster.setClusterId("CL-00001");
            savedCluster.setCreatedAt(Instant.now());

            when(clusterRepository.findByClusterName("Test Cluster"))
                    .thenReturn(Optional.empty());

            when(clusterRepository.save(any(Cluster.class)))
                    .thenReturn(savedCluster);

            // when
            ClusterCreateResponse response = clusterService.createCluster(request);

            // then
            assertNotNull(response);
            assertEquals("CL-00001", response.clusterId());
            assertEquals("Test Cluster", response.clusterName());
            assertNotNull(response.createdAt());

            verify(clusterRepository).findByClusterName("Test Cluster");
            verify(clusterRepository).save(any(Cluster.class));
        }

        @Test
        @DisplayName("Should throw ConflictException when cluster name already exists")
        void shouldThrowExceptionWhenClusterAlreadyExists() {
            // given
            ClusterCreateRequest request = new ClusterCreateRequest("Test Cluster");

            when(clusterRepository.findByClusterName("Test Cluster"))
                    .thenReturn(Optional.of(new Cluster()));

            // when & then
            ConflictException ex = assertThrows(
                    ConflictException.class,
                    () -> clusterService.createCluster(request)
            );

            assertEquals("CLUSTER_ALREADY_EXISTS", ex.getError().getCode());
            verify(clusterRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw BadRequestException when cluster name is null")
        void shouldThrowExceptionWhenClusterNameIsNull() {
            // given
            ClusterCreateRequest request = new ClusterCreateRequest(null);

            // when & then
            BadRequestException ex = assertThrows(
                    BadRequestException.class,
                    () -> clusterService.createCluster(request)
            );

            assertEquals("CLUSTER_REQUEST_INVALID", ex.getError().getCode());
            verifyNoInteractions(clusterRepository);
        }

        @Test
        @DisplayName("Should throw BadRequestException when cluster name is blank")
        void shouldThrowExceptionWhenClusterNameIsBlank() {
            // given
            ClusterCreateRequest request = new ClusterCreateRequest("   ");

            // when & then
            BadRequestException ex = assertThrows(
                    BadRequestException.class,
                    () -> clusterService.createCluster(request)
            );

            assertEquals("CLUSTER_NAME_REQUIRED", ex.getError().getCode());
            verifyNoInteractions(clusterRepository);
        }
    }

    @Nested
    @DisplayName("Find By Cluster Id")
    class FindByClusterId {

        @Test
        @DisplayName("SUCCESS: Get cluster with devices")
        void findByClusterId_success() {
            // Arrange
            String clusterId = "CL-001";
            Cluster cluster = createCluster(clusterId);

            List<Device> devices = List.of(
                    createDevice("DEV-1"),
                    createDevice("DEV-2")
            );

            when(clusterRepository.findByClusterId(clusterId))
                    .thenReturn(Optional.of(cluster));
            when(deviceRepository.findAllDevicesByClusterId(clusterId))
                    .thenReturn(devices);

            // Act
            ClusterGetResponse response = clusterService.findByClusterId(clusterId);

            // Assert
            assertNotNull(response);
            assertEquals(clusterId, response.clusterId());
            assertEquals("Test Cluster", response.clusterName());
            assertEquals(2, response.totalDevices());
            assertEquals(2, response.devices().size());

            verify(clusterRepository, times(1)).findByClusterId(clusterId);
            verify(deviceRepository, times(1)).findAllDevicesByClusterId(clusterId);
        }

        @Test
        @DisplayName("FAIL: Cluster not found")
        void findByClusterId_notFound() {
            // Arrange
            String clusterId = "CL-404";
            when(clusterRepository.findByClusterId(clusterId))
                    .thenReturn(Optional.empty());

            // Act & Assert
            NotFoundException exception = assertThrows(
                    NotFoundException.class,
                    () -> clusterService.findByClusterId(clusterId)
            );

            assertEquals("CLUSTER_NOT_FOUND", exception.getError().getCode());

            verify(clusterRepository, times(1)).findByClusterId(clusterId);
            verify(deviceRepository, never()).findAllDevicesByClusterId(any());
        }

        @Test
        @DisplayName("FAIL: Cluster already deleted")
        void findByClusterId_alreadyDeleted() {
            // Arrange
            String clusterId = "CL-003";
            Cluster deletedCluster = createCluster(clusterId);
            deletedCluster.setDeletedAt(Instant.now());

            when(clusterRepository.findByClusterId(clusterId))
                    .thenReturn(Optional.of(deletedCluster));

            // Act & Assert
            ConflictException exception = assertThrows(
                    ConflictException.class,
                    () -> clusterService.findByClusterId(clusterId)
            );

            assertEquals("CLUSTER_ALREADY_DELETED", exception.getError().getCode());

            verify(clusterRepository, times(1)).findByClusterId(clusterId);
            verify(deviceRepository, never()).findAllDevicesByClusterId(any());
        }

        @Test
        @DisplayName("SUCCESS: Cluster without devices returns empty list")
        void findByClusterId_noDevices() {
            // Arrange
            String clusterId = "CL-004";
            Cluster cluster = createCluster(clusterId);

            when(clusterRepository.findByClusterId(clusterId))
                    .thenReturn(Optional.of(cluster));
            when(deviceRepository.findAllDevicesByClusterId(clusterId))
                    .thenReturn(List.of());

            // Act
            ClusterGetResponse response = clusterService.findByClusterId(clusterId);

            // Assert
            assertNotNull(response);
            assertEquals(0, response.totalDevices());
            assertTrue(response.devices().isEmpty());
        }

        private Cluster createCluster(String clusterId) {
            Cluster cluster = new Cluster();
            cluster.setClusterId(clusterId);
            cluster.setClusterName("Test Cluster");
            cluster.setDeletedAt(null);
            return cluster;
        }

        private Device createDevice(String deviceId) {
            Device device = new Device();
            device.setDeviceId(deviceId);
            return device;
        }
    }

    @Nested
    @DisplayName("Update Cluster")
    class UpdateCluster {

        @Test
        @DisplayName("SUCCESS: Update cluster name")
        void updateCluster_success() {
            // Arrange
            String clusterId = "CL-100";
            ClusterUpdateRequest request = new ClusterUpdateRequest("New Cluster Name");

            Cluster cluster = createCluster(clusterId);

            when(clusterRepository.findByClusterId(clusterId))
                    .thenReturn(Optional.of(cluster));

            // Act
            ClusterUpdateResponse response = clusterService.updateCluster(clusterId, request);

            // Assert
            assertNotNull(response);
            assertEquals(clusterId, response.clusterId());
            assertEquals("New Cluster Name", response.clusterName());
            assertNotNull(response.updatedAt());

            verify(clusterRepository, times(1)).findByClusterId(clusterId);
        }

        @Test
        @DisplayName("FAIL: Cluster not found")
        void updateCluster_clusterNotFound() {
            // Arrange
            String clusterId = "CL-404";
            ClusterUpdateRequest request = new ClusterUpdateRequest("New Name");

            when(clusterRepository.findByClusterId(clusterId))
                    .thenReturn(Optional.empty());

            // Act & Assert
            NotFoundException exception = assertThrows(
                    NotFoundException.class,
                    () -> clusterService.updateCluster(clusterId, request)
            );

            assertEquals("CLUSTER_NOT_FOUND", exception.getError().getCode());
        }

        @Test
        @DisplayName("FAIL: Request is null")
        void updateCluster_nullRequest() {
            // Arrange
            String clusterId = "CL-001";

            // Act & Assert
            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> clusterService.updateCluster(clusterId, null)
            );

            assertEquals("CLUSTER_REQUEST_INVALID", exception.getError().getCode());
        }

        @Test
        @DisplayName("FAIL: No fields provided to update")
        void updateCluster_emptyUpdate() {
            // Arrange
            String clusterId = "CL-002";
            Cluster cluster = createCluster(clusterId);

            ClusterUpdateRequest request = new ClusterUpdateRequest(null);

            when(clusterRepository.findByClusterId(clusterId))
                    .thenReturn(Optional.of(cluster));

            // Act & Assert
            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> clusterService.updateCluster(clusterId, request)
            );

            assertEquals("CLUSTER_UPDATE_EMPTY", exception.getError().getCode());
        }

        @Test
        @DisplayName("SUCCESS: Only name is updated, other fields remain unchanged")
        void updateCluster_partialUpdate() {
            // Arrange
            String clusterId = "CL-200";
            Cluster cluster = createCluster(clusterId);
            Instant oldUpdatedAt = cluster.getUpdatedAt();

            ClusterUpdateRequest request = new ClusterUpdateRequest("Updated Name");

            when(clusterRepository.findByClusterId(clusterId))
                    .thenReturn(Optional.of(cluster));

            // Act
            ClusterUpdateResponse response = clusterService.updateCluster(clusterId, request);

            // Assert
            assertEquals("Updated Name", response.clusterName());
            assertNotEquals(oldUpdatedAt, response.updatedAt());
        }

        private Cluster createCluster(String clusterId) {
            Cluster cluster = new Cluster();
            cluster.setClusterId(clusterId);
            cluster.setClusterName("Old Cluster Name");
            cluster.setUpdatedAt(Instant.now());
            return cluster;
        }

    }

    @Nested
    @DisplayName("Delete Cluster")
    class DeleteCluster {

        @Test
        @DisplayName("SUCCESS: Delete cluster successfully when cluster exists and not deleted")
        void deleteCluster_success() {
            // Arrange
            String clusterId = "CL-00001";
            Cluster cluster = createActiveCluster(clusterId);

            when(clusterRepository.findByClusterId(clusterId))
                    .thenReturn(Optional.of(cluster));

            // Act
            clusterService.deleteCluster(clusterId);

            // Assert
            verify(clusterRepository, times(1)).softDelete(clusterId);
            verify(deviceRepository, times(1)).softDeleteDevicesByClusterId(clusterId);
            verify(clusterRepository, times(1)).findByClusterId(clusterId);
        }

        @Test
        @DisplayName("FAIL: Cluster not found")
        void deleteCluster_clusterNotFound() {
            // Arrange
            String clusterId = "CL-404";

            when(clusterRepository.findByClusterId(clusterId))
                    .thenReturn(Optional.empty());

            // Act & Assert
            NotFoundException exception = assertThrows(
                    NotFoundException.class,
                    () -> clusterService.deleteCluster(clusterId)
            );

            assertEquals("CLUSTER_NOT_FOUND", exception.getError().getCode());

            verify(clusterRepository, never()).softDelete(any());
            verify(deviceRepository, never()).softDeleteDevicesByClusterId(any());
        }

        @Test
        @DisplayName("FAIL: Cluster already deleted")
        void deleteCluster_alreadyDeleted() {
            // Arrange
            String clusterId = "CL-00002";
            Cluster deletedCluster = createDeletedCluster(clusterId);

            when(clusterRepository.findByClusterId(clusterId))
                    .thenReturn(Optional.of(deletedCluster));

            // Act & Assert
            ConflictException exception = assertThrows(
                    ConflictException.class,
                    () -> clusterService.deleteCluster(clusterId)
            );

            assertEquals("CLUSTER_ALREADY_DELETED", exception.getError().getCode());

            verify(clusterRepository, never()).softDelete(any());
            verify(deviceRepository, never()).softDeleteDevicesByClusterId(any());
        }

        @Test
        @DisplayName("Ensure device deletion is not executed when cluster deletion fails")
        void deleteCluster_deviceDeletionNotCalledOnFailure() {
            // Arrange
            String clusterId = "CL-FAIL";
            when(clusterRepository.findByClusterId(clusterId))
                    .thenReturn(Optional.empty());

            // Act
            try {
                clusterService.deleteCluster(clusterId);
            } catch (Exception ignored) {}

            // Assert
            verify(deviceRepository, never()).softDeleteDevicesByClusterId(any());
        }

        private Cluster createActiveCluster(String clusterId) {
            Cluster cluster = new Cluster();
            cluster.setClusterId(clusterId);
            cluster.setDeletedAt(null);
            return cluster;
        }

        private Cluster createDeletedCluster(String clusterId) {
            Cluster cluster = new Cluster();
            cluster.setClusterId(clusterId);
            cluster.setDeletedAt(Instant.now());
            return cluster;
        }
    }
}
