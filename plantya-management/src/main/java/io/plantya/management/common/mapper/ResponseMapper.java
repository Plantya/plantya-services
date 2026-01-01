package io.plantya.management.common.mapper;

import io.plantya.management.dto.response.UserCreatedResponse;
import io.plantya.management.dto.response.UserDeletedResponse;
import io.plantya.management.dto.response.UserResponse;
import io.plantya.management.dto.response.UserUpdatedResponse;
import io.plantya.management.entity.User;

public class ResponseMapper {

    public static UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getEmail(),
                user.getName(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public static UserCreatedResponse toUserCreatedResponse(User user) {
        return new UserCreatedResponse(
                user.getEmail(),
                user.getName(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

    public static UserDeletedResponse toUserDeletedResponse(User user) {
        return new UserDeletedResponse(
                user.getUserId(),
                user.getEmail(),
                user.getName(),
                user.getRole(),
                user.getDeletedAt()
        );
    }

    public static UserUpdatedResponse toUserUpdatedResponse(User user) {
        return new UserUpdatedResponse(
                user.getUserId(),
                user.getEmail(),
                user.getName(),
                user.getRole(),
                user.getUpdatedAt()
        );
    }

}
