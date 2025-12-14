package io.plantya.management.dto.response;

import java.util.List;

public record ListUserResponse<T>(
        long countData,
        int page,
        int size,
        int totalPages,
        List<T> data
) {}
