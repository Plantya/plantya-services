package io.plantya.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.plantya.user.domain.UserRole;

public record UserUpdateRequest(
        @JsonProperty("email") String email,
        @JsonProperty("name") String name,
        @JsonProperty("role") UserRole role,
        @JsonProperty("password") String password
) {}