package io.plantya.management.service;

import io.plantya.management.dto.GetUserDto;
import io.plantya.management.dto.request.CreateUserRequest;
import io.plantya.management.dto.response.DeletedUserResponse;
import io.plantya.management.dto.response.UserCreatedResponse;
import io.plantya.management.dto.response.UserResponse;
import io.plantya.management.dto.response.UserUpdatedResponse;
import io.plantya.management.entity.User;
import io.plantya.management.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

import static io.plantya.management.util.UserManagementValidator.validateRequest;

@ApplicationScoped
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public List<UserResponse> listUsers() {
        return repository.findActiveUserList();
    }

    public GetUserDto getUser(String userId) {
        return repository.findUserByUserId(userId);
    }

    public UserCreatedResponse createUser(CreateUserRequest request) {
        validateRequest(request);

        char userIdPrefix = switch (request.role()) {
            case USER -> 'U';
            case STAFF -> 'S';
            case ADMIN -> 'A';
        };

        var user = new User();
        user.setUserId(userIdPrefix + "0001");
        user.setEmail(request.email());
        user.setName(request.name());
        user.setRole(request.role());
        user.setHashedPassword(request.password());

        repository.createUser(user);

        return new UserCreatedResponse(
                user.getEmail(),
                user.getName(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

    public List<DeletedUserResponse> listDeletedUsers() {
        return repository.findDeletedUserList();
    }

    public DeletedUserResponse findDeletedUserById(String userId) {
        return repository.findDeletedUserByUserId(userId);
    }

    public UserResponse restoreUser(String userId) {
        repository.restoreDeletedUserByUserId(userId);

        return null;
    }

    public void replaceUser(UserResponse user) {

    }

}
