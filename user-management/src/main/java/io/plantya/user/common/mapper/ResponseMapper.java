package io.plantya.user.common.mapper;

import io.plantya.user.domain.User;
import io.plantya.user.dto.response.UserCreateResponse;
import io.plantya.user.dto.response.UserDeletedResponse;
import io.plantya.user.dto.response.UserGetResponse;
import io.plantya.user.dto.response.UserUpdateResponse;

public class ResponseMapper {

    public static UserCreateResponse toUserCreateResponse(User user) {
        return new UserCreateResponse(
                user.getEmail(),
                user.getName(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

    public static UserGetResponse toUserGetResponse(User user) {
        return new UserGetResponse(
                user.getUserId(),
                user.getEmail(),
                user.getName(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public static UserUpdateResponse toUserUpdatedResponse(User user) {
        return new UserUpdateResponse(
                user.getUserId(),
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
}
