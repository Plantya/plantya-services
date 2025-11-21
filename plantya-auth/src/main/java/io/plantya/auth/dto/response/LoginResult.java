package io.plantya.auth.dto.response;

import io.plantya.auth.entity.User;

public record LoginResult(AppResponse<LoginResponse> response, User user) {}
