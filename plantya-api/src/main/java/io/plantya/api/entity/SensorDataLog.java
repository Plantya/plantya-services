package io.plantya.api.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.OffsetDateTime;

@Entity
@Table(name = "sensor_data_log")
public class SensorDataLog extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "device_id", nullable = false, length = 64)
    public String deviceId;

    @Column(name = "greenhouse_id", nullable = false, length = 64)
    public String greenhouseId;

    @Column(name = "timestamp", nullable = false)
    public Instant timestamp;

    @Column(name = "temperature")
    public Double temperature;

    @Column(name = "humidity")
    public Double humidity;

    @Column(name = "soil_moisture")
    public Double soilMoisture;

    @Column(name = "created_at")
    public Instant createdAt;

}
