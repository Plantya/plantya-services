package io.plantya.user.common.dto.query;

import java.util.List;

public record QueryData(String query, List<Object> params) {}
