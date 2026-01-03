package io.plantya.user.service;

import io.plantya.user.common.dto.param.UserParam;
import io.plantya.user.common.dto.request.UserQueryParam;
import io.plantya.user.common.exception.BadRequestException;
import io.plantya.user.common.exception.ConflictException;
import io.plantya.user.common.exception.NotFoundException;
import io.plantya.user.common.mapper.ResponseMapper;
import io.plantya.user.common.validator.RequestValidator;
import io.plantya.user.config.PasswordService;
import io.plantya.user.domain.User;
import io.plantya.user.dto.request.UserCreateRequest;
import io.plantya.user.dto.request.UserUpdateRequest;
import io.plantya.user.dto.response.UserUpdateResponse;
import io.plantya.user.dto.response.PagedUserResponse;
import io.plantya.user.dto.response.UserCreateResponse;
import io.plantya.user.dto.response.UserGetResponse;
import io.plantya.user.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.List;

import static io.plantya.user.common.exception.message.ErrorMessage.*;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    @Inject
    PasswordService passwordService;

    private final Logger LOG = Logger.getLogger(UserService.class);

    public PagedUserResponse<UserGetResponse> findAllExistingUsers(UserQueryParam param) {
        if (param.getPage() < 1) {
            throw new BadRequestException(PAGE_LOWER_THAN_ONE);
        }

        UserParam userParam = new UserParam(
                param.getPage(),
                param.getSize(),
                param.getSearch(),
                param.getSort(),
                param.getOrder(),
                param.getRole()
        );

        List<User> devices = userRepository.findAllExistingUsers(userParam);
        long totalData = userRepository.countExistingUsers(userParam);

        List<UserGetResponse> responses = devices.stream()
                .map(ResponseMapper::toUserGetResponse)
                .toList();

        int totalPages = (int) Math.ceil((double) totalData / param.getSize());

        return new PagedUserResponse<>(
                responses.size(),
                param.getPage(),
                param.getSize(),
                totalPages,
                responses
        );
    }

    @Transactional
    public UserCreateResponse createUser(UserCreateRequest request) {
        RequestValidator.validateCreateRequest(request);

        LOG.infof(
                "Creating user: email=%s, name=%s, role=%s",
                request.email(), request.name(), request.role()
        );

        boolean isEmailUsed = userRepository.findUserByEmail(request.email()).isPresent();
        if (isEmailUsed) {
            LOG.warnf("Create user failed - email already exists: %s", request.email());
            throw new ConflictException(USER_EMAIL_ALREADY_EXISTS);
        }

        String password = generateDefaultPassword(request.name());

        User user = new User();
        user.setEmail(request.email());
        user.setName(request.name());
        user.setRole(request.role());
        user.setHashedPassword(passwordService.hash(password));

        User savedUser = userRepository.save(user);

        LOG.infof("User created successfully: userId=%s", savedUser.getUserId());
        return ResponseMapper.toUserCreateResponse(savedUser);
    }

    public UserGetResponse findByUserId(String userId) {
        User user = userRepository.findByUserId(userId).orElse(null);
        if (user == null) {
            throw new NotFoundException(USER_NOT_FOUND);
        }

        return ResponseMapper.toUserGetResponse(user);
    }

    @Transactional
    public UserUpdateResponse updateUser(String userId, UserUpdateRequest request) {
        LOG.infof("Patch user: userId=%s", userId);

        User user = userRepository.findByUserId(userId).orElse(null);
        if (user == null) {
            LOG.warnf("Patch user failed - user not found: userId=%s", userId);
            throw new NotFoundException(USER_NOT_FOUND);
        }

        if (user.getDeletedAt() != null) {
            LOG.warnf("Patch user failed - already deleted: userId=%s", userId);
            throw new ConflictException(USER_ALREADY_DELETED);
        }

        boolean isUpdated = false;

        if (request.email() != null) {
            user.setEmail(request.email());
            isUpdated = true;
        }

        if (request.name() != null) {
            user.setName(request.name());
            isUpdated = true;
        }

        if (request.role() != null) {
            user.setRole(request.role());
            isUpdated = true;
        }

        if (!isUpdated) {
            LOG.warnf("Patch user ignored - no fields provided: userId=%s", userId);
            throw new BadRequestException(USER_PATCH_EMPTY);
        }

        LOG.debugf(
                "Patch fields: email=%s, name=%s, role=%s",
                request.email(),
                request.name(),
                request.role()
        );

        user.setUpdatedAt(Instant.now());

        LOG.infof("User patched successfully: userId=%s", userId);
        return ResponseMapper.toUserUpdatedResponse(user);
    }

    @Transactional
    public void deleteUser(String userId) {
        LOG.infof("Delete user request: userId=%s", userId);

        User user = userRepository.findByUserId(userId).orElse(null);
        if (user == null) {
            LOG.warnf("Delete user failed - user not found: userId=%s", userId);
            throw new NotFoundException(USER_NOT_FOUND);
        }

        if (user.getDeletedAt() != null) {
            LOG.warnf("Delete user failed - already deleted: userId=%s", userId);
            throw new ConflictException(USER_ALREADY_DELETED);
        }

        userRepository.softDelete(user.getUserId());
        LOG.infof("User deleted successfully: userId=%s", userId);
    }

    private String generateDefaultPassword(String name) {
        return "plantya_" + name.split(" ")[0];
    }
}
