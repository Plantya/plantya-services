package com.agrosentinel.auth.dto.response;

import com.agrosentinel.auth.entity.User;

public record LoginResult(AppResponse<LoginResponse> response, User user) {}
