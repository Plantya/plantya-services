CREATE TABLE clusters (
    id BIGSERIAL PRIMARY KEY,
    cluster_id VARCHAR(20) NOT NULL UNIQUE,
    cluster_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE TABLE devices (
    id BIGSERIAL PRIMARY KEY,
    device_id VARCHAR(20) NOT NULL UNIQUE,
    device_name VARCHAR(255) NOT NULL,
    device_type VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL,
    cluster_id VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_devices_cluster_id ON devices(cluster_id);
CREATE INDEX idx_devices_device_id ON devices(device_id);
