package com.agrosentinel.auth.dto.response;

import com.agrosentinel.auth.dto.response.AppResponse;
import com.agrosentinel.auth.dto.response.LoginResponse;
import com.agrosentinel.auth.entity.User;

public record LoginResult(AppResponse<LoginResponse> response, User user) {}
