package io.plantya.management.service;

import io.plantya.management.dto.request.UserRequest;
import io.plantya.management.dto.response.ListUserResponse;
import io.plantya.management.dto.response.UserDeletedResponse;
import io.plantya.management.dto.response.UserCreatedResponse;
import io.plantya.management.dto.response.UserResponse;
import io.plantya.management.entity.User;
import io.plantya.management.enums.UserBadRequestMessage;
import io.plantya.management.enums.UserRole;
import io.plantya.management.exception.BadRequestException;
import io.plantya.management.repository.UserRepository;
import io.plantya.management.util.UserManagementValidator;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class UserService {

    private final UserManagementValidator validator = new UserManagementValidator();
    private final Logger LOG = Logger.getLogger(UserService.class);

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public ListUserResponse<UserResponse> findAllActive(
            Integer page,
            Integer size,
            String sort,
            String order,
            String search,
            UserRole role
    ) {
        boolean usePaging = page != null || size != null;

        if (usePaging && (page == null || size == null)) {
            throw new BadRequestException(UserBadRequestMessage.USER_PAGINATION_PARAMETER_INCOMPLETE);
        }

        if (usePaging && (page < 1 || size < 1)) {
            throw new BadRequestException(UserBadRequestMessage.USER_PAGINATION_PARAMETER_INVALID);
        }

        if (order != null && !order.isBlank()) {
            if (!Objects.equals(order.toUpperCase(), "ASC") && !Objects.equals(order.toUpperCase(), "DESC")) {
                throw new IllegalArgumentException("Invalid order value");
            }
        }

        boolean useSearch = search != null && !search.isBlank();
        boolean useRoleFilter = role != null;

        long count = repository.countActiveUser(useSearch ? search : null, useRoleFilter ? role : null);

        if (!usePaging) {
            List<UserResponse> list = repository.findAllActive(
                    sort,
                    order,
                    useSearch ? search : null,
                    useRoleFilter ? role : null
            );

            return new ListUserResponse<>(count, 1, list.size(), 1, list);
        }

        List<UserResponse> list = repository.findAllActive(
                page,
                size,
                sort,
                order,
                useSearch ? search : null,
                useRoleFilter ? role : null
        );

        int totalPages = (int) Math.ceil((double) count / size);

        return new ListUserResponse<>(count, page, size, totalPages, list);
    }

    public UserResponse findById(String userId) {
        return repository.findById(userId);
    }

    public UserCreatedResponse createUser(UserRequest request) {
        validator.validateCreateRequest(request);

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

    public List<UserDeletedResponse> listDeletedUsers() {
        return repository.findDeletedUserList();
    }

    public UserDeletedResponse findDeletedUserById(String userId) {
        return repository.findDeletedUserByUserId(userId);
    }

    public UserResponse restoreUser(String userId) {
        return null;
    }

    public void replaceUser(UserResponse user) {

    }

}
