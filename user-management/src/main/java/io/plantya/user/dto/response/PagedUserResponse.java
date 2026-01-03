package io.plantya.user.dto.response;

import java.util.List;

public record PagedUserResponse<T>(
        long countData,
        int page,
        int size,
        int totalPages,
        List<T> users
) {}
