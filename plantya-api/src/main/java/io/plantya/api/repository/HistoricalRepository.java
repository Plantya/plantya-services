package io.plantya.api.repository;

import io.plantya.api.entity.SensorDataLog;
import io.plantya.api.exception.NotFoundException;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.ConfigProvider;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@ApplicationScoped
public class HistoricalRepository implements PanacheRepository<SensorDataLog> {

    private final String zone = ConfigProvider.getConfig().getValue("app.zone", String.class);

    public SensorDataLog findLatestDataByDeviceId(String deviceId) {
        return find("deviceId = ?1 ORDER BY timestamp DESC", deviceId).firstResult();
    }

    public List<SensorDataLog> findSensorDataByDeviceAndTimeRange(String deviceId, LocalDate from, LocalDate to) {
        ZoneId zoneId = ZoneId.of(zone);

        Instant startDate = from.atStartOfDay(zoneId).toInstant();
        Instant endDate = to.atStartOfDay(zoneId).plusDays(1).minusNanos(1).toInstant();

        List<SensorDataLog> sensorDataList = find(
                "deviceId = ?1 AND timestamp BETWEEN ?2 AND ?3 ORDER BY timestamp ASC",
                deviceId,
                startDate,
                endDate
        ).list();

        if (sensorDataList.isEmpty()) {
            throw new NotFoundException("Sensor Data Not Found");
        }

        return sensorDataList;
    }

}
