package io.plantya.user.common.dto.param;

import io.plantya.user.domain.UserRole;

public record UserParam(
        int page,
        int size,
        String search,
        String sort,
        String order,
        UserRole role
) {}
