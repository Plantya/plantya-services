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
import io.plantya.management.exception.NotFoundException;
import io.plantya.management.repository.UserRepository;
import io.plantya.management.util.UserManagementValidator;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Objects;

import static io.plantya.management.enums.UserBadRequestMessage.USER_INVALID_REQUEST_PAYLOAD;

@ApplicationScoped
public class UserService {

    private final UserManagementValidator validator = new UserManagementValidator();
    private final Logger LOG = Logger.getLogger(UserService.class);

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves a list of all active users, optionally paginated, filtered, and sorted.
     *
     * @param page   the page number to retrieve, starting from 1. If specified, size must also be provided.
     * @param size   the number of records per page. If specified, page must also be provided.
     * @param sort   the field by which to sort the results.
     * @param order  the order of sorting, either "ASC" for ascending or "DESC" for descending.
     * @param search a search keyword to filter users by name or other attributes.
     * @param role   the role to filter users (e.g., USER, STAFF, ADMIN).
     * @return a {@code ListUserResponse<UserResponse>} containing the active users along with pagination details.
     * @throws BadRequestException if pagination parameters are incomplete or invalid.
     * @throws IllegalArgumentException if the order parameter is invalid.
     */
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

    /**
     * Deletes a user by adding time on their deleted_at in the repository.
     * This method performs a soft delete, ensuring the user data is not permanently removed
     * but flagged as deleted.
     *
     * @param userId the unique identifier of the user to be deleted
     * @return the {@code UserDeletedResponse} containing information about the deleted user
     * @throws NotFoundException if the user with the given ID does not exist or is not found
     * @throws RuntimeException if an error occurs during the deletion process
     */
    public UserDeletedResponse deleteUser(String userId) {
        UserDeletedResponse user = repository.findDeletedUserByUserId(userId);

        if (user == null) {
            throw new NotFoundException(USER_INVALID_REQUEST_PAYLOAD);
        }

        try {
            repository.softDelete(userId);
        } catch (Exception e) {
            LOG.error("Error while deleting user: " + userId, e);
            throw new RuntimeException("Error while deleting user: " + userId + ".", e.getCause());
        }

        return user;
    }

}
