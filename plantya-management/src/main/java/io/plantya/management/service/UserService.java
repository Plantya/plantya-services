package io.plantya.management.service;

import io.plantya.management.common.util.PaginationUtil;
import io.plantya.management.common.util.PasswordUtil;
import io.plantya.management.common.util.UserGeneratorUtil;
import io.plantya.management.dto.request.UserPatchRequest;
import io.plantya.management.dto.request.UserRequest;
import io.plantya.management.dto.response.*;
import io.plantya.management.entity.User;
import io.plantya.management.enums.UserConflictMessage;
import io.plantya.management.enums.UserRole;
import io.plantya.management.exception.BadRequestException;
import io.plantya.management.exception.ConflictException;
import io.plantya.management.exception.NotFoundException;
import io.plantya.management.repository.UserRepository;
import io.plantya.management.common.mapper.ResponseMapper;
import io.plantya.management.common.validator.UserManagementValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.List;

import static io.plantya.management.enums.UserBadRequestMessage.USER_PATCH_EMPTY;
import static io.plantya.management.enums.UserErrorMessage.USER_ALREADY_ACTIVE;
import static io.plantya.management.enums.UserErrorMessage.USER_ALREADY_DELETED;
import static io.plantya.management.enums.UserNotFoundMessage.USER_NOT_FOUND;

@ApplicationScoped
public class UserService {

    private final Logger LOG = Logger.getLogger(UserService.class);

    private final UserManagementValidator validator;
    private final UserRepository repository;

    public UserService(UserManagementValidator validator, UserRepository repository) {
        this.validator = validator;
        this.repository = repository;
    }

    /**
     * Retrieves a list of all active users, optionally paginated, filtered, and sorted.
     *
     * @param page   the page number to retrieve, starting from 1. If specified, size must also be provided.
     * @param size   the number of records per page. If specified, a page must also be provided.
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
        boolean usePaging = PaginationUtil.validateAndUsePaging(page, size);
        PaginationUtil.validateOrder(order);

        boolean useSearch = search != null && !search.isBlank();
        boolean useRoleFilter = role != null;

        long count = repository.countActiveUsers(useSearch ? search : null, useRoleFilter ? role : null);

        if (!usePaging) {
            List<UserResponse> list = repository.findAllActive(
                    null,
                    null,
                    sort,
                    order,
                    useSearch ? search : null,
                    useRoleFilter ? role : null
            );

            return new ListUserResponse<>(count, 1, list.size(), 1, list);
        }

        List<UserResponse> list = repository.findAllActive(
                null,
                null,
                sort,
                order,
                search,
                role
        );

        int totalPages = (int) Math.ceil((double) count / size);

        LOG.debugf("Active users found: count=%d", count);
        LOG.debugf(
                "Find active users: page=%s, size=%s, sort=%s, order=%s, search=%s, role=%s",
                page, size, sort, order, search, role
        );

        return new ListUserResponse<>(count, page, size, totalPages, list);
    }

    /**
     * Retrieves a user by their unique identifier and converts it into a response object.
     * If the user is not found, a {@code NotFoundException} is thrown.
     *
     * @param userId the unique identifier of the user to be retrieved
     * @return a {@code UserResponse} object containing the user's details
     * @throws NotFoundException if the user with the given ID is not found
     */
    public UserResponse findById(String userId) {
        User user = getUser(userId);

        if (user.getDeletedAt() != null) {
            LOG.warnf("Find user by id failed - user is deleted: %s", userId);
            throw new NotFoundException(USER_NOT_FOUND);
        }

        LOG.debugf("Find user by id: %s", userId);
        return ResponseMapper.toUserResponse(user);
    }

    /**
     * Updates partial user information based on the provided request.
     * This method validates the input and ensures that the user exists and is not yet deleted.
     * At least one valid field in the request must be provided for the update to proceed.
     *
     * @param userId the unique identifier of the user to be patched
     * @param request the request object containing partial user updates
     *                - email: new email address of the user
     *                - name: new name of the user
     *                - role: new role of the user
     * @return a {@code UserUpdatedResponse} containing the updated user details
     * @throws NotFoundException if the user with the given ID does not exist
     * @throws ConflictException if the user has already been deleted
     * @throws BadRequestException if the provided patch request does not contain any updatable fields
     */
    @Transactional
    public UserUpdatedResponse patchUser(String userId, UserPatchRequest request) {
        LOG.infof("Patch user: userId=%s", userId);

        User user = repository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        if (user.getDeletedAt() != null) {
            throw new ConflictException(USER_ALREADY_DELETED);
        }

        boolean isUpdated = false;

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
            isUpdated = true;
        }

        if (request.getName() != null) {
            user.setName(request.getName());
            isUpdated = true;
        }

        if (request.getRole() != null) {
            user.setRole(request.getRole());
            isUpdated = true;
        }

        if (!isUpdated) {
            LOG.warnf("Patch user ignored - no fields provided: userId=%s", userId);
            throw new BadRequestException(USER_PATCH_EMPTY);
        }

        LOG.debugf(
                "Patch fields: email=%s, name=%s, role=%s",
                request.getEmail(),
                request.getName(),
                request.getRole()
        );

        user.setUpdatedAt(Instant.now());

        LOG.infof("User patched successfully: userId=%s", userId);
        return ResponseMapper.toUserUpdatedResponse(user);
    }


    /**
     * Creates a new user based on the provided request details. This method validates the request,
     * generates a unique user ID based on the user's role, and persists the user in the repository.
     * If the email already exists, a conflict exception is thrown.
     *
     * @param request the user creation request containing email, name, and role
     * @return a {@code UserCreatedResponse} object containing the details of the created user
     * @throws ConflictException if the provided email already exists in the repository
     * @throws BadRequestException if the request fails validation
     */
    @Transactional
    public UserCreatedResponse createUser(UserRequest request) {
        if (repository.isEmailExists(request.email())) {
            LOG.warnf("Create user failed - email already exists: %s", request.email());
            throw new ConflictException(UserConflictMessage.USER_EMAIL_ALREADY_EXISTS);
        }

        LOG.infof(
                "Create user request: email=%s, role=%s",
                request.email(),
                request.role()
        );

        validator.validateCreateRequest(request);

        long lastIndex = repository.getLastUserIndex().orElse(0L);
        String userId = UserGeneratorUtil.generateUserId(request.role(), lastIndex);

        String password = UserGeneratorUtil.generateDefaultPassword(userId);

        User user = new User();
        user.setUserId(userId);
        user.setEmail(request.email());
        user.setName(request.name());
        user.setRole(request.role());
        user.setHashedPassword(PasswordUtil.hash(password));

        repository.createUser(user);

        LOG.infof("User created successfully: userId=%s", userId);
        return ResponseMapper.toUserCreatedResponse(user);
    }

    /**
     * Deletes the specified user by performing a soft delete operation.
     * If the user does not exist or is already deleted, appropriate exceptions are thrown.
     *
     * @param userId the unique identifier of the user to be deleted
     * @throws NotFoundException if the user with the given ID is not found
     * @throws ConflictException if the user with the given ID is already deleted
     * @throws RuntimeException if an error occurs during the delete operation
     */
    @Transactional
    public void deleteUser(String userId) {
        LOG.infof("Delete user request: userId=%s", userId);

        User user = repository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        if (user.getDeletedAt() != null) {
            LOG.warnf("Delete user failed - already deleted: userId=%s", userId);
            throw new ConflictException(USER_ALREADY_DELETED);
        }

        repository.softDelete(userId);

        LOG.infof("User deleted successfully: userId=%s", userId);
    }

    @Transactional
    public UserResponse restoreUser(String userId) {
        LOG.infof("Restore user request: userId=%s", userId);

        User user = repository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        if (user.getDeletedAt() == null) {
            LOG.warnf("Restore user failed - already active: userId=%s", userId);
            throw new ConflictException(USER_ALREADY_ACTIVE);
        }

        repository.restoreUser(userId);

        LOG.infof("User restored successfully: userId=%s", userId);
        return ResponseMapper.toUserResponse(user);
    }

    public ListUserResponse<UserDeletedResponse> findAllDeleted(
            Integer page,
            Integer size,
            String sort,
            String order,
            String search,
            UserRole role
    ) {
        boolean usePaging = PaginationUtil.validateAndUsePaging(page, size);
        PaginationUtil.validateOrder(order);

        boolean useSearch = search != null && !search.isBlank();
        boolean useRoleFilter = role != null;

        long count = repository.countDeletedUser(
                useSearch ? search : null,
                useRoleFilter ? role : null
        );

        if (!usePaging) {
            List<UserDeletedResponse> list = repository.findAllDeleted(null, null, sort, order, search, role);
            return new ListUserResponse<>(count, 1, list.size(), 1, list);
        }

        List<UserDeletedResponse> list = repository.findAllDeleted(page, size, sort, order, search, role);

        int totalPages = (int) Math.ceil((double) count / size);

        LOG.debugf("Deleted users found: count=%d", count);
        LOG.debugf(
                "Find deleted users: page=%s, size=%s, sort=%s, order=%s, search=%s, role=%s",
                page, size, sort, order, search, role
        );
        return new ListUserResponse<>(count, page, size, totalPages, list);
    }

    public UserDeletedResponse findDeletedById(String userId) {
        User user = getUser(userId);

        if (user.getDeletedAt() == null) {
            LOG.warnf("Find deleted user failed - user is not deleted: %s", userId);
            throw new NotFoundException(USER_NOT_FOUND);
        }

        LOG.debugf("Find user by id: %s", userId);
        return ResponseMapper.toUserDeletedResponse(user);
    }

    private User getUser(String userId) {
        return repository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }

}
