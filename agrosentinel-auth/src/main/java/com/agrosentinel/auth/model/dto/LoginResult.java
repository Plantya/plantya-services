package com.agrosentinel.auth.model.dto;

import com.agrosentinel.auth.model.entity.User;

public record LoginResult(AppResponse<LoginResponse> response, User user) {}
