package io.plantya.iot.device.repository;

import io.plantya.iot.device.domain.Device;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
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

    public List<Device> findAllExistingDevices(int page, int size, String search) {
        StringBuilder query = new StringBuilder("deletedAt IS NULL");

        if (search != null && !search.isBlank()) {
            query.append(" AND (")
                    .append("LOWER(clusterId) LIKE ?1 ")
                    .append("OR LOWER(deviceName) LIKE ?1 ")
                    .append("OR LOWER(deviceType) LIKE ?1 ")
                    .append(")");
        }

        if (search != null && !search.isBlank()) {
            return find(query.toString(), "%" + search.toLowerCase() + "%")
                    .page(Page.of(page, size))
                    .list();
        }

        return find(query.toString())
                .page(Page.of(page, size))
                .list();
    }

    public long countExistingDevices(String search) {
        String query = "deletedAt IS NULL";

        if (search == null || search.isBlank()) {
            return count(query);
        }

        return count("""
                deletedAt IS NULL AND (
                    LOWER(clusterId) LIKE ?1 OR
                    LOWER(deviceName) LIKE ?1 OR
                    LOWER(deviceType) LIKE ?1
                )
                """,
                "%" + search.toLowerCase() + "%"
        );
    }

    public void softDelete(String deviceId) {
        update(
                "deletedAt = :deletedAt WHERE deviceId = :deviceId AND deletedAt IS NULL",
                Parameters.with("deletedAt", Instant.now())
                        .and("deviceId", deviceId)
        );
    }
}
