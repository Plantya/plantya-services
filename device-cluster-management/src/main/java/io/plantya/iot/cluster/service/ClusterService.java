package io.plantya.iot.cluster.service;

import io.plantya.iot.cluster.dto.request.ClusterCreateRequest;
import io.plantya.iot.cluster.dto.request.ClusterUpdateRequest;
import io.plantya.iot.cluster.dto.response.*;
import io.plantya.iot.cluster.entity.Cluster;
import io.plantya.iot.cluster.repository.ClusterRepository;
import io.plantya.iot.common.dto.request.ClusterQueryParam;
import io.plantya.iot.common.exception.BadRequestException;
import io.plantya.iot.common.exception.ConflictException;
import io.plantya.iot.common.exception.NotFoundException;
import io.plantya.iot.common.mapper.ClusterResponseMapper;
import io.plantya.iot.common.validator.RequestValidator;
import io.plantya.iot.device.domain.Device;
import io.plantya.iot.device.repository.DeviceRepository;
import io.plantya.iot.device.service.DeviceService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.List;

import static io.plantya.iot.common.exception.message.ErrorMessage.*;

@ApplicationScoped
public class ClusterService {

    @Inject
    ClusterRepository clusterRepository;

    @Inject
    DeviceRepository deviceRepository;

    private final Logger LOG = Logger.getLogger(DeviceService.class);

    public PagedClusterResponse findAllExistingClusters(ClusterQueryParam queryParam) {
        if (queryParam.getPage() < 1) {
            throw new BadRequestException(PAGE_LOWER_THAN_ONE);
        }

        List<Cluster> clusterList = clusterRepository.findAllExistingClusters(queryParam);
        long totalData = clusterRepository.countExistingDevices(queryParam);

        List<ClusterListResponse> responses = clusterList.stream()
                .map(ClusterResponseMapper::toClusterListResponse)
                .toList();

        int totalPages = (int) Math.ceil((double) totalData / queryParam.getSize());

        return new PagedClusterResponse(
                responses.size(),
                queryParam.getPage(),
                queryParam.getSize(),
                totalPages,
                responses
        );
    }

    @Transactional
    public ClusterCreateResponse createCluster(ClusterCreateRequest request) {
        LOG.infof("Creating cluster: clusterName=%s", request.clusterName());

        RequestValidator.validateClusterCreateRequest(request);

        boolean isClusterNameExists = clusterRepository.findByClusterName(request.clusterName()).isPresent();
        if (isClusterNameExists) {
            throw new ConflictException(CLUSTER_ALREADY_EXISTS);
        }

        Cluster cluster = new Cluster();
        cluster.setClusterName(request.clusterName());

        Cluster savedCluster = clusterRepository.save(cluster);

        LOG.infof("Cluster created successfully: clusterId=%s", savedCluster.getClusterId());
        return ClusterResponseMapper.toClusterCreateResponse(savedCluster);
    }

    public ClusterGetResponse findByClusterId(String clusterId) {
        Cluster cluster = clusterRepository.findByClusterId(clusterId).orElse(null);
        if (cluster == null) {
            throw new NotFoundException(CLUSTER_NOT_FOUND);
        }

        if (cluster.getDeletedAt() != null) {
            throw new ConflictException(CLUSTER_ALREADY_DELETED);
        }

        List<Device> devices = deviceRepository.findAllDevicesByClusterId(clusterId);

        return ClusterResponseMapper.toClusterGetResponse(cluster, devices);
    }

    @Transactional
    public ClusterUpdateResponse updateCluster(String clusterId, ClusterUpdateRequest request) {
        LOG.infof("Patch device: clusterId=%s", clusterId);

        RequestValidator.validateClusterUpdateRequest(request);

        Cluster cluster = clusterRepository.findByClusterId(clusterId).orElse(null);
        if (cluster == null) {
            LOG.warnf("Cluster not found: clusterId=%s", clusterId);
            throw new NotFoundException(CLUSTER_NOT_FOUND);
        }

        boolean isUpdated = false;

        if (request.clusterName() != null) {
            cluster.setClusterName(request.clusterName());
            isUpdated = true;
        }

        if (!isUpdated) {
            LOG.warnf("Patch cluster ignored - no fields provided: clusterId=%s", clusterId);
            throw new BadRequestException(CLUSTER_UPDATE_EMPTY);
        }

        LOG.debugf("Patch fields: clusterName=%s", request.clusterName());

        cluster.setUpdatedAt(Instant.now());

        LOG.infof("Cluster patched successfully: clusterId=%s", clusterId);
        return ClusterResponseMapper.toClusterUpdateResponse(cluster);
    }

    @Transactional
    public void deleteCluster(String clusterId) {
        LOG.infof("Deleting cluster: clusterId=%s", clusterId);

        Cluster cluster = clusterRepository.findByClusterId(clusterId).orElse(null);
        if (cluster == null) {
            LOG.warnf("Cluster not found: clusterId=%s", clusterId);
            throw new NotFoundException(CLUSTER_NOT_FOUND);
        }

        if (cluster.getDeletedAt() != null) {
            LOG.warnf("Cluster already deleted: clusterId=%s", clusterId);
            throw new ConflictException(CLUSTER_ALREADY_DELETED);
        }

        clusterRepository.softDelete(clusterId);
        deviceRepository.softDeleteDevicesByClusterId(clusterId);
        LOG.infof("Cluster deleted successfully: clusterId=%s", clusterId);
    }
}
