package io.plantya.iot.device.repository;

import io.plantya.iot.common.dto.param.DeviceParam;
import io.plantya.iot.device.domain.Device;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class DeviceRepository implements PanacheRepository<Device> {

    public Device save(Device device) {
        persist(device);
        flush();
        getEntityManager().refresh(device);

        return device;
    }

    public Optional<Device> findByDeviceId(String deviceId) {
        return find("deviceId", deviceId).firstResultOptional();
    }

    public List<Device> findAllExistingDevices(DeviceParam param) {
        QueryData queryData = buildQuery(param);

        return find(queryData.query(), queryData.params().toArray())
                .page(Page.of(param.page() - 1, param.size()))
                .list();
    }

    public long countExistingDevices(DeviceParam param) {
        QueryData queryData = buildQuery(param);
        return count(queryData.query(), queryData.params().toArray());
    }

    public void softDelete(String deviceId) {
        update(
                "deletedAt = :deletedAt WHERE deviceId = :deviceId AND deletedAt IS NULL",
                Parameters.with("deletedAt", Instant.now())
                        .and("deviceId", deviceId)
        );
    }

    // ===== HELPER ===== //
    private QueryData buildQuery(DeviceParam param) {
        StringBuilder query = new StringBuilder("deletedAt IS NULL");
        List<Object> params = new ArrayList<>();

        // Search
        if (param.search() != null && !param.search().isBlank()) {
            query.append("""
                AND (
                    LOWER(clusterId) LIKE ?1
                    OR LOWER(deviceName) LIKE ?1
                    OR LOWER(deviceType) LIKE ?1
                )
            """);
            params.add("%" + param.search().toLowerCase() + "%");
        }

        // Status filter
        if (param.status() != null) {
            query.append(" AND status = ?").append(params.size() + 1);
            params.add(param.status());
        }

        // Sorting
        query.append(" ORDER BY ")
                .append(resolveSortColumn(param.sort()))
                .append(" ")
                .append(resolveSortOrder(param.order()));

        return new QueryData(query.toString(), params);
    }

    private String resolveSortColumn(String sort) {
        if (sort == null) return "createdAt";

        return switch (sort) {
            case "deviceName" -> "deviceName";
            case "deviceType" -> "deviceType";
            case "clusterId" -> "clusterId";
            case "createdAt" -> "createdAt";
            default -> "createdAt";
        };
    }

    private String resolveSortOrder(String order) {
        return "asc".equalsIgnoreCase(order) ? "ASC" : "DESC";
    }

    private record QueryData(String query, List<Object> params) {}
}
