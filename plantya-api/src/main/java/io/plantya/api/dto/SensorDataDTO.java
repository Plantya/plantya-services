package io.plantya.api.dto;

public record SensorDataDTO(
        String timestamp,
        String deviceId,
        String greenhouseId,
        Double temperature,
        Double humidity,
        Double soilMoisture
) {
}
