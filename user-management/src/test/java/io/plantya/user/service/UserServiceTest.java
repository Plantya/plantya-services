package io.plantya.user.service;

import io.plantya.user.common.dto.param.UserParam;
import io.plantya.user.common.dto.request.UserQueryParam;
import io.plantya.user.common.exception.BadRequestException;
import io.plantya.user.common.exception.ConflictException;
import io.plantya.user.common.exception.NotFoundException;
import io.plantya.user.common.exception.message.ErrorMessage;
import io.plantya.user.config.PasswordService;
import io.plantya.user.domain.User;
import io.plantya.user.domain.UserRole;
import io.plantya.user.dto.request.UserCreateRequest;
import io.plantya.user.dto.request.UserUpdateRequest;
import io.plantya.user.dto.response.PagedUserResponse;
import io.plantya.user.dto.response.UserCreateResponse;
import io.plantya.user.dto.response.UserGetResponse;
import io.plantya.user.dto.response.UserUpdateResponse;
import io.plantya.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Test")
class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordService passwordService;

    @Nested
    @DisplayName("FindAllExistingUsers")
    class FindAllExistingUsers {

        @Test
        @DisplayName("Should return paged existing users successfully")
        void shouldReturnPagedExistingUsersSuccessfully() {
            // Arrange
            UserQueryParam param = new UserQueryParam();
            param.setPage(1);
            param.setSize(10);
            param.setSearch(null);
            param.setSort(null);
            param.setOrder(null);
            param.setRole(null);

            User user1 = new User();
            user1.setUserId("user-1");
            user1.setEmail("user1@mail.com");
            user1.setName("User One");
            user1.setRole(UserRole.ADMIN);
            user1.setCreatedAt(Instant.now().minusSeconds(3600));
            user1.setUpdatedAt(Instant.now());
            user1.setDeletedAt(null);

            User user2 = new User();
            user2.setUserId("user-2");
            user2.setEmail("user2@mail.com");
            user2.setName("User Two");
            user2.setRole(UserRole.USER);
            user2.setCreatedAt(Instant.now().minusSeconds(7200));
            user2.setUpdatedAt(Instant.now());
            user2.setDeletedAt(null);

            List<User> users = List.of(user1, user2);

            when(userRepository.findAllExistingUsers(any(UserParam.class)))
                    .thenReturn(users);

            when(userRepository.countExistingUsers(any(UserParam.class)))
                    .thenReturn(2L);

            // Act
            PagedUserResponse<UserGetResponse> response =
                    userService.findAllExistingUsers(param);

            // Assert
            assertNotNull(response);
            assertEquals(2, response.countData());
            assertEquals(1, response.page());
            assertEquals(10, response.size());
            assertEquals(1, response.totalPages());
            assertEquals(2, response.users().size());

            UserGetResponse firstUser = response.users().getFirst();
            assertEquals("user-1", firstUser.userId());
            assertEquals("user1@mail.com", firstUser.email());
            assertEquals("User One", firstUser.name());
            assertEquals(UserRole.ADMIN, firstUser.role());
            assertNotNull(firstUser.createdAt());
            assertNotNull(firstUser.updatedAt());

            verify(userRepository).findAllExistingUsers(any(UserParam.class));
            verify(userRepository).countExistingUsers(any(UserParam.class));
        }

        @Test
        @DisplayName("Should throw BadRequestException when page is lower than one")
        void shouldThrowBadRequestWhenPageLowerThanOne() {
            // Arrange
            UserQueryParam param = new UserQueryParam();
            param.setPage(0);
            param.setSize(10);

            // Act & Assert
            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> userService.findAllExistingUsers(param)
            );

            assertEquals(
                    ErrorMessage.PAGE_LOWER_THAN_ONE.getDefaultDetail(),
                    exception.getDetail()
            );

            verifyNoInteractions(userRepository);
        }

        @Test
        @DisplayName("Should return empty users when no existing users found")
        void shouldReturnEmptyUsersWhenNoData() {
            // Arrange
            UserQueryParam param = new UserQueryParam();
            param.setPage(1);
            param.setSize(10);

            when(userRepository.findAllExistingUsers(any(UserParam.class)))
                    .thenReturn(List.of());

            when(userRepository.countExistingUsers(any(UserParam.class)))
                    .thenReturn(0L);

            // Act
            PagedUserResponse<UserGetResponse> response =
                    userService.findAllExistingUsers(param);

            // Assert
            assertNotNull(response);
            assertEquals(0, response.countData());
            assertEquals(1, response.page());
            assertEquals(10, response.size());
            assertEquals(0, response.totalPages());
            assertTrue(response.users().isEmpty());

            verify(userRepository).findAllExistingUsers(any(UserParam.class));
            verify(userRepository).countExistingUsers(any(UserParam.class));
        }
    }

    @Nested
    @DisplayName("CreateUser")
    class CreateUser {

        @Test
        @DisplayName("Should create user successfully when email not exists")
        void shouldCreateUserSuccessfully() {
            // Arrange
            UserCreateRequest request = new UserCreateRequest(
                    "newuser@mail.com",
                    "New User",
                    UserRole.USER
            );

            when(userRepository.findUserByEmail(request.email()))
                    .thenReturn(Optional.empty());

            when(passwordService.hash("plantya_New"))
                    .thenReturn("hashed-password");

            User savedUser = new User();
            savedUser.setUserId("user-123");
            savedUser.setEmail(request.email());
            savedUser.setName(request.name());
            savedUser.setRole(request.role());
            savedUser.setCreatedAt(Instant.now());

            when(userRepository.save(any(User.class)))
                    .thenReturn(savedUser);

            // Act
            UserCreateResponse response = userService.createUser(request);

            // Assert
            assertNotNull(response);
            assertEquals("newuser@mail.com", response.email());
            assertEquals("New User", response.name());
            assertEquals(UserRole.USER, response.role());
            assertNotNull(response.createdAt());

            verify(userRepository).findUserByEmail(request.email());
            verify(passwordService).hash("plantya_New");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw ConflictException when email already exists")
        void shouldThrowConflictWhenEmailAlreadyExists() {
            // Arrange
            UserCreateRequest request = new UserCreateRequest(
                    "existing@mail.com",
                    "Existing User",
                    UserRole.ADMIN
            );

            when(userRepository.findUserByEmail(request.email()))
                    .thenReturn(Optional.of(new User()));

            // Act & Assert
            ConflictException exception = assertThrows(
                    ConflictException.class,
                    () -> userService.createUser(request)
            );

            assertEquals(
                    ErrorMessage.USER_EMAIL_ALREADY_EXISTS.getDefaultDetail(),
                    exception.getDetail()
            );

            verify(userRepository).findUserByEmail(request.email());
            verify(userRepository, never()).save(any());
            verifyNoInteractions(passwordService);
        }
    }

    @Nested
    @DisplayName("FindByUserId")
    class FindByUserId {

        @Test
        @DisplayName("Should return user when user exists")
        void shouldReturnUserWhenExists() {
            // Arrange
            String userId = "user-123";

            User user = new User();
            user.setUserId(userId);
            user.setEmail("user@mail.com");
            user.setName("Existing User");
            user.setRole(UserRole.USER);
            user.setCreatedAt(Instant.now().minusSeconds(3600));
            user.setUpdatedAt(Instant.now());

            when(userRepository.findByUserId(userId))
                    .thenReturn(Optional.of(user));

            // Act
            UserGetResponse response =
                    userService.findByUserId(userId);

            // Assert
            assertNotNull(response);
            assertEquals(userId, response.userId());
            assertEquals("user@mail.com", response.email());
            assertEquals("Existing User", response.name());
            assertEquals(UserRole.USER, response.role());
            assertNotNull(response.createdAt());
            assertNotNull(response.updatedAt());

            verify(userRepository).findByUserId(userId);
        }

        @Test
        @DisplayName("Should throw NotFoundException when user not found")
        void shouldThrowNotFoundWhenUserNotFound() {
            // Arrange
            String userId = "user-not-found";

            when(userRepository.findByUserId(userId))
                    .thenReturn(Optional.empty());

            // Act & Assert
            NotFoundException exception = assertThrows(
                    NotFoundException.class,
                    () -> userService.findByUserId(userId)
            );

            assertEquals(
                    ErrorMessage.USER_NOT_FOUND.getDefaultDetail(),
                    exception.getDetail()
            );

            verify(userRepository).findByUserId(userId);
        }
    }

    @Nested
    @DisplayName("UpdateUser")
    class UpdateUser {

        @Test
        @DisplayName("Should update user successfully when email is provided")
        void shouldUpdateUserWhenEmailProvided() {
            // Arrange
            String userId = "user-1";

            UserUpdateRequest request = new UserUpdateRequest(
                    "new@mail.com",
                    null,
                    null,
                    null
            );

            User user = new User();
            user.setUserId(userId);
            user.setEmail("old@mail.com");
            user.setName("Old Name");
            user.setRole(UserRole.USER);
            user.setCreatedAt(Instant.now().minusSeconds(3600));
            user.setDeletedAt(null);

            when(userRepository.findByUserId(userId))
                    .thenReturn(Optional.of(user));

            // Act
            UserUpdateResponse response = userService.updateUser(userId, request);

            // Assert
            assertNotNull(response);
            assertEquals(userId, response.userId());
            assertEquals("new@mail.com", response.email());
            assertEquals("Old Name", response.name());
            assertEquals(UserRole.USER, response.role());

            verify(userRepository).findByUserId(userId);
        }

        @Test
        @DisplayName("Should update user successfully when multiple fields are provided")
        void shouldUpdateUserWhenMultipleFieldsProvided() {
            // Arrange
            String userId = "user-2";

            UserUpdateRequest request = new UserUpdateRequest(
                    "updated@mail.com",
                    "Updated Name",
                    UserRole.ADMIN,
                    null
            );

            User user = new User();
            user.setUserId(userId);
            user.setEmail("old@mail.com");
            user.setName("Old Name");
            user.setRole(UserRole.USER);
            user.setCreatedAt(Instant.now().minusSeconds(3600));
            user.setDeletedAt(null);

            when(userRepository.findByUserId(userId))
                    .thenReturn(Optional.of(user));

            // Act
            UserUpdateResponse response =
                    userService.updateUser(userId, request);

            // Assert
            assertNotNull(response);
            assertEquals("updated@mail.com", response.email());
            assertEquals("Updated Name", response.name());
            assertEquals(UserRole.ADMIN, response.role());

            verify(userRepository).findByUserId(userId);
        }

        @Test
        @DisplayName("Should throw NotFoundException when user not found")
        void shouldThrowNotFoundWhenUserNotFound() {
            // Arrange
            String userId = "user-not-found";
            UserUpdateRequest request = new UserUpdateRequest(
                    "mail@mail.com",
                    null,
                    null,
                    null
            );

            when(userRepository.findByUserId(userId))
                    .thenReturn(Optional.empty());

            // Act & Assert
            NotFoundException exception = assertThrows(
                    NotFoundException.class,
                    () -> userService.updateUser(userId, request)
            );

            assertEquals(
                    ErrorMessage.USER_NOT_FOUND.getDefaultDetail(),
                    exception.getDetail()
            );

            verify(userRepository).findByUserId(userId);
        }

        @Test
        @DisplayName("Should throw ConflictException when user already deleted")
        void shouldThrowConflictWhenUserAlreadyDeleted() {
            // Arrange
            String userId = "user-deleted";

            UserUpdateRequest request = new UserUpdateRequest(
                    "mail@mail.com",
                    null,
                    null,
                    null
            );

            User user = new User();
            user.setUserId(userId);
            user.setDeletedAt(Instant.now());

            when(userRepository.findByUserId(userId))
                    .thenReturn(Optional.of(user));

            // Act & Assert
            ConflictException exception = assertThrows(
                    ConflictException.class,
                    () -> userService.updateUser(userId, request)
            );

            assertEquals(
                    ErrorMessage.USER_ALREADY_DELETED.getDefaultDetail(),
                    exception.getDetail()
            );

            verify(userRepository).findByUserId(userId);
        }

        @Test
        @DisplayName("Should throw BadRequestException when no fields are provided")
        void shouldThrowBadRequestWhenNoFieldsProvided() {
            // Arrange
            String userId = "user-empty";

            UserUpdateRequest request = new UserUpdateRequest(
                    null,
                    null,
                    null,
                    null
            );

            User user = new User();
            user.setUserId(userId);
            user.setDeletedAt(null);

            when(userRepository.findByUserId(userId))
                    .thenReturn(Optional.of(user));

            // Act & Assert
            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> userService.updateUser(userId, request)
            );

            assertEquals(
                    ErrorMessage.USER_PATCH_EMPTY.getDefaultDetail(),
                    exception.getDetail()
            );

            verify(userRepository).findByUserId(userId);
        }
    }

    @Nested
    @DisplayName("DeleteUser")
    class DeleteUser {

        @Test
        @DisplayName("Should delete user successfully when user exists and active")
        void shouldDeleteUserSuccessfully() {
            // Arrange
            String userId = "user-1";

            User user = new User();
            user.setUserId(userId);
            user.setDeletedAt(null);

            when(userRepository.findByUserId(userId))
                    .thenReturn(Optional.of(user));

            // Act
            userService.deleteUser(userId);

            // Assert
            verify(userRepository).findByUserId(userId);
            verify(userRepository).softDelete(userId);
        }

        @Test
        @DisplayName("Should throw NotFoundException when user not found")
        void shouldThrowNotFoundWhenUserNotFound() {
            // Arrange
            String userId = "user-not-found";

            when(userRepository.findByUserId(userId))
                    .thenReturn(Optional.empty());

            // Act & Assert
            NotFoundException exception = assertThrows(
                    NotFoundException.class,
                    () -> userService.deleteUser(userId)
            );

            assertEquals(
                    ErrorMessage.USER_NOT_FOUND.getDefaultDetail(),
                    exception.getDetail()
            );

            verify(userRepository).findByUserId(userId);
            verify(userRepository, never()).softDelete(any());
        }

        @Test
        @DisplayName("Should throw ConflictException when user already deleted")
        void shouldThrowConflictWhenUserAlreadyDeleted() {
            // Arrange
            String userId = "user-deleted";

            User user = new User();
            user.setUserId(userId);
            user.setDeletedAt(Instant.now());

            when(userRepository.findByUserId(userId))
                    .thenReturn(Optional.of(user));

            // Act & Assert
            ConflictException exception = assertThrows(
                    ConflictException.class,
                    () -> userService.deleteUser(userId)
            );

            assertEquals(
                    ErrorMessage.USER_ALREADY_DELETED.getDefaultDetail(),
                    exception.getDetail()
            );

            verify(userRepository).findByUserId(userId);
            verify(userRepository, never()).softDelete(any());
        }
    }
}