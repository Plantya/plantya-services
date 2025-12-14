package io.plantya.api.service;

import io.plantya.api.dto.SensorDataDTO;
import io.plantya.api.entity.SensorDataLog;
import io.plantya.api.exception.InternalServerErrorException;
import io.plantya.api.exception.NotFoundException;
import io.plantya.api.repository.HistoricalRepository;
import io.plantya.api.exception.BadRequestException;
import io.plantya.api.util.AppLogger;
import io.plantya.api.util.RequestValidator;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.ConfigProvider;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class HistoricalService {

    private final String zone = ConfigProvider.getConfig().getValue("app.zone", String.class);

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS");
    private final HistoricalRepository repository;

    public HistoricalService(HistoricalRepository repository) {
        this.repository = repository;
    }

    public List<SensorDataDTO> getHistoricalData(String deviceId, LocalDate from, LocalDate to) {
        AppLogger.info(
                "Getting historical data for '" + deviceId +
                "' from " + formatter.format(from.atStartOfDay()) +
                " to " + formatter.format(to.atStartOfDay().plusDays(1).minusNanos(1))
        );

        try {
            RequestValidator.validate(deviceId, from, to);

            var result = new ArrayList<SensorDataDTO>();

            List<SensorDataLog> sensorDataList = repository.findSensorDataByDeviceAndTimeRange(deviceId, from, to);
            sensorDataList.forEach(data -> {
                var sensorData = new SensorDataDTO(
                        formatter.withZone(ZoneId.of(zone)).format(data.timestamp),
                        data.deviceId,
                        data.greenhouseId,
                        data.temperature,
                        data.humidity,
                        data.soilMoisture
                );

                result.add(sensorData);
            });

            return result;
        } catch (BadRequestException e) {
            AppLogger.error(e.getMessage(), e);
            throw new BadRequestException(e.getMessage());
        } catch (NotFoundException e) {
            AppLogger.error(e.getMessage(), e);
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            AppLogger.error(e.getMessage(), e);
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    public SensorDataDTO getLatestData(String deviceId) {
        AppLogger.info("Getting latest data for device " + deviceId);

        try {
            RequestValidator.validate(deviceId);

            SensorDataLog data = repository.findLatestDataByDeviceId(deviceId);
            if (data == null) {
                throw new NotFoundException("No sensor data found for device " + deviceId);
            }

            return new SensorDataDTO(
                    formatter.withZone(ZoneId.of(zone)).format(data.timestamp),
                    data.deviceId,
                    data.greenhouseId,
                    data.temperature,
                    data.humidity,
                    data.soilMoisture
            );
        } catch (BadRequestException e) {
            AppLogger.error(e.getMessage(), e);
            throw new BadRequestException(e.getMessage());
        } catch (NotFoundException e) {
            AppLogger.error(e.getMessage(), e);
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            AppLogger.error(e.getMessage(), e);
            throw new InternalServerErrorException("Internal server error");
        }
    }

}
