package io.plantya.iot.common.dto.query;

import java.util.List;

public record QueryData(String query, List<Object> params) {}
