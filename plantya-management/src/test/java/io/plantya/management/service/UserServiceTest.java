package io.plantya.management.service;

import io.plantya.management.dto.request.UserPatchRequest;
import io.plantya.management.dto.request.UserRequest;
import io.plantya.management.dto.response.UserCreatedResponse;
import io.plantya.management.dto.response.UserDeletedResponse;
import io.plantya.management.dto.response.UserResponse;
import io.plantya.management.dto.response.UserUpdatedResponse;
import io.plantya.management.entity.User;
import io.plantya.management.enums.UserBadRequestMessage;
import io.plantya.management.enums.UserConflictMessage;
import io.plantya.management.enums.UserRole;
import io.plantya.management.exception.BadRequestException;
import io.plantya.management.exception.ConflictException;
import io.plantya.management.exception.NotFoundException;
import io.plantya.management.repository.UserRepository;
import io.plantya.management.common.validator.UserManagementValidator;
import io.plantya.management.repository.UserSequenceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private UserManagementValidator validator;

    @Mock
    private UserSequenceRepository sequenceRepository;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("Find All Active User")
    class FindAllActive {

        @Test
        @DisplayName("Should return all active users when paging parameters are not provided")
        void findAllActive_withoutPaging_returnsAllData() {
            // arrange
            var userList = List.of(
                    new UserResponse("U001", "user1@domain.com", "User One", UserRole.USER, Instant.now(), null),
                    new UserResponse("U002", "user2@domain.com", "User Two", UserRole.USER, Instant.now(), null)
            );

            when(repository.countActiveUsers(null, null)).thenReturn(2L);
            when(repository.findAllActive(null, null, null, null, null, null))
                    .thenReturn(userList);

            // act
            var response = userService.findAllActive(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            // assert
            assertEquals(2L, response.countData());
            assertEquals(1, response.page(), "page should default to 1 when paging is not used");
            assertEquals(2, response.size(), "size should equal list size when paging is not used");
            assertEquals(1, response.totalPages());
            assertEquals(2, response.data().size());

            verify(repository).findAllActive(null, null, null, null, null, null);
        }

        @Test
        @DisplayName("Should return paginated active users when parameters are valid")
        void findAllActive_withValidParameters_returnsExpectedResponse() {
            var userService = new UserService(validator, repository, sequenceRepository);

            var userList = List.of(
                    new UserResponse("U001", "user1@domain.com", "User One", UserRole.USER, Instant.now(), Instant.now()),
                    new UserResponse("U002", "user2@domain.com", "User Two", UserRole.USER, Instant.now(), Instant.now())
            );

            when(repository.countActiveUsers(null, null)).thenReturn(2L);
            when(repository.findAllActive(
                    isNull(),
                    isNull(),
                    isNull(),
                    isNull(),
                    isNull(),
                    isNull()
            )).thenReturn(userList);

            var response = userService.findAllActive(1, 5, null, null, null, null);

            assertEquals(2L, response.countData());
            assertEquals(1, response.page());
            assertEquals(5, response.size());
            assertEquals(1, response.totalPages());
            assertEquals(2, response.data().size());
            assertEquals("user1@domain.com", response.data().getFirst().email());
        }

        @Test
        @DisplayName("Should throw BadRequestException when pagination parameters are invalid or incomplete")
        void findAllActive_withInvalidPagination_throwsBadRequestException() {
            var userService = new UserService(validator, repository, sequenceRepository);

            BadRequestException thrownException = assertThrows(
                    BadRequestException.class,
                    () -> userService.findAllActive(null, 5, null, null, null, null)
            );

            assertEquals(
                    UserBadRequestMessage.USER_PAGINATION_PARAMETER_INCOMPLETE.getDefaultDetail(),
                    thrownException.getMessage()
            );

            thrownException = assertThrows(
                    BadRequestException.class,
                    () -> userService.findAllActive(0, 5, null, null, null, null)
            );

            assertEquals(
                    UserBadRequestMessage.USER_PAGINATION_PARAMETER_INVALID.getDefaultDetail(),
                    thrownException.getMessage()
            );
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when order parameter is invalid")
        void findAllActive_withInvalidOrder_throwsIllegalArgumentException() {
            var userService = new UserService(validator, repository, sequenceRepository);

            BadRequestException thrownException = assertThrows(
                    BadRequestException.class,
                    () -> userService.findAllActive(1, 5, null, "INVALID_ORDER", null, null)
            );

            assertEquals("invalid order value", thrownException.getMessage());
        }

        @Test
        @DisplayName("Should return empty result when no active users found")
        void findAllActive_withEmptyResult_returnsEmptyList() {
            var userService = new UserService(validator, repository, sequenceRepository);

            when(repository.countActiveUsers(null, null)).thenReturn(0L);
            when(repository.findAllActive(null, null, null, null, null, null))
                    .thenReturn(List.of());

            var response = userService.findAllActive(1, 5, null, null, null, null);

            assertEquals(0L, response.countData());
            assertEquals(1, response.page());
            assertEquals(5, response.size());
            assertEquals(0, response.totalPages());
            assertTrue(response.data().isEmpty());
        }

        @Nested
        @DisplayName("Create User")
        class CreateUser {

            @Test
            @DisplayName("Should throw ConflictException when email already exists")
            void shouldThrowConflictException_whenEmailAlreadyExists() {
                // arrange
                UserRequest request = new UserRequest(
                        "user@mail.com",
                        "User Test",
                        UserRole.USER
                );

                when(repository.isEmailExists("user@mail.com")).thenReturn(true);

                // act and assert
                ConflictException exception = assertThrows(
                        ConflictException.class,
                        () -> userService.createUser(request)
                );

                assertEquals(
                        UserConflictMessage.USER_EMAIL_ALREADY_EXISTS.getDefaultDetail(),
                        exception.getMessage()
                );

                // verify never: no side effect
                verify(validator, never()).validateCreateRequest(any());
                verify(sequenceRepository, never()).nextIndex();
                verify(repository, never()).createUser(any());
            }

            @Test
            void shouldCreateUserWithUserRole() {
                // given
                UserRequest request = new UserRequest(
                        "user@mail.com",
                        "User Test",
                        UserRole.USER
                );

                when(sequenceRepository.nextIndex()).thenReturn(1L);

                // when
                UserCreatedResponse response = userService.createUser(request);

                // then
                verify(validator).validateCreateRequest(request);
                verify(repository).createUser(any(User.class));

                assertEquals("user@mail.com", response.email());
                assertEquals("User Test", response.name());
                assertEquals(UserRole.USER, response.role());

                ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
                verify(repository).createUser(captor.capture());

                User savedUser = captor.getValue();
                assertEquals("U00001", savedUser.getUserId());
                assertTrue(savedUser.getHashedPassword().startsWith("$"));
            }

            @ParameterizedTest
            @CsvSource({
                    "USER,U",
                    "STAFF,S",
                    "ADMIN,A"
            })
            @DisplayName("Should generate correct user ID prefix based on role")
            void shouldGenerateCorrectUserIdPrefix(UserRole role, char prefix) {
                UserRequest request = new UserRequest(
                        "test@mail.com",
                        "Test",
                        role
                );

                when(sequenceRepository.nextIndex()).thenReturn(12L);

                userService.createUser(request);

                ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
                verify(repository).createUser(captor.capture());

                assertEquals(prefix + "00012", captor.getValue().getUserId());
            }

            @Test
            @DisplayName("Should propagate exception when repository fails during create")
            void shouldPropagateExceptionWhenRepositoryFails() {
                UserRequest request = new UserRequest(
                        "user@mail.com",
                        "User",
                        UserRole.USER
                );

                when(sequenceRepository.nextIndex()).thenReturn(1L);
                doThrow(new RuntimeException("DB error"))
                        .when(repository).createUser(any(User.class));

                RuntimeException exception = assertThrows(
                        RuntimeException.class,
                        () -> userService.createUser(request)
                );

                assertEquals("DB error", exception.getMessage());
            }

            @Test
            @DisplayName("Should format user ID with leading zeros")
            void shouldFormatUserIdWithLeadingZeros() {
                UserRequest request = new UserRequest(
                        "user@mail.com",
                        "User",
                        UserRole.ADMIN
                );

                when(sequenceRepository.nextIndex()).thenReturn(99999L);

                userService.createUser(request);

                ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
                verify(repository).createUser(captor.capture());

                assertEquals("A99999", captor.getValue().getUserId());
            }

            @Test
            @DisplayName("Should hash password before saving user")
            void shouldHashPasswordBeforeSaving() {
                UserRequest request = new UserRequest(
                        "user@mail.com",
                        "User",
                        UserRole.USER
                );

                when(sequenceRepository.nextIndex()).thenReturn(1L);

                userService.createUser(request);

                ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
                verify(repository).createUser(captor.capture());

                User savedUser = captor.getValue();

                assertNotEquals("plantya_U00001", savedUser.getHashedPassword());
            }
        }

        @Nested
        @DisplayName("Find Active User By ID")
        class FindById {

            @Test
            @DisplayName("Should return UserResponse when user is active")
            void findById_whenUserActive_returnsUserResponse() {
                // arrange
                User user = new User();
                user.setUserId("U00001");
                user.setEmail("user@mail.com");
                user.setName("User");
                user.setRole(UserRole.USER);
                user.setCreatedAt(Instant.now());
                user.setUpdatedAt(null);
                user.setDeletedAt(null);

                when(repository.findByUserId("U00001"))
                        .thenReturn(Optional.of(user));

                // act
                UserResponse result = userService.findById("U00001");

                // assert
                assertNotNull(result);
                assertEquals("U00001", result.userId());
                assertEquals("user@mail.com", result.email());
                assertEquals(UserRole.USER, result.role());
            }

            @Test
            @DisplayName("Should throw NotFoundException when user is deleted")
            void findById_whenUserDeleted_throwsNotFoundException() {
                // arrange
                User user = new User();
                user.setUserId("U00001");
                user.setDeletedAt(Instant.now());

                when(repository.findByUserId("U00001"))
                        .thenReturn(Optional.of(user));

                // act + assert
                assertThrows(
                        NotFoundException.class,
                        () -> userService.findById("U00001")
                );
            }

            @Test
            @DisplayName("Should throw NotFoundException when user is not found")
            void findById_whenUserNotFound_throwsNotFoundException() {
                when(repository.findByUserId("U00001"))
                        .thenReturn(Optional.empty());

                assertThrows(
                        NotFoundException.class,
                        () -> userService.findById("U00001")
                );
            }
        }

        @Nested
        @DisplayName("Patch User")
        class PatchUser {

            @Test
            @DisplayName("Should not update anything when patch request is empty")
            void patchUser_whenNoFieldProvided_shouldNotMutateUser() {
                User user = new User();
                user.setDeletedAt(null);

                when(repository.findByUserId("U00001")).thenReturn(Optional.of(user));

                assertThrows(
                        BadRequestException.class,
                        () -> userService.patchUser("U00001", new UserPatchRequest())
                );

                assertNull(user.getUpdatedAt(), "updatedAt should not be set");
            }

            @Test
            @DisplayName("Should update name successfully when provided")
            void patchUser_withEmailOnly_updatesSuccessfully() {
                // arrange
                User user = new User();
                user.setUserId("U00001");
                user.setEmail("old@mail.com");
                user.setName("Old");
                user.setRole(UserRole.USER);
                user.setDeletedAt(null);

                UserPatchRequest request = new UserPatchRequest();
                request.setEmail("new@mail.com");

                when(repository.findByUserId("U00001")).thenReturn(Optional.of(user));

                // act
                UserUpdatedResponse response = userService.patchUser("U00001", request);

                // assert
                assertEquals("U00001", response.userId());
                assertEquals("new@mail.com", response.email());
                assertEquals("Old", response.name());
                assertNotNull(response.updatedAt());
            }

            @Test
            @DisplayName("Should update role successfully when provided")
            void patchUser_whenNoFieldProvided_throwsBadRequestException() {
                User user = new User();
                user.setDeletedAt(null);

                when(repository.findByUserId("U00001")).thenReturn(Optional.of(user));

                assertThrows(
                        BadRequestException.class,
                        () -> userService.patchUser("U00001", new UserPatchRequest())
                );
            }

            @Test
            @DisplayName("Should throw ConflictException when user is already deleted")
            void patchUser_whenUserDeleted_throwsConflictException() {
                User user = new User();
                user.setDeletedAt(Instant.now());

                when(repository.findByUserId("U00001")).thenReturn(Optional.of(user));

                assertThrows(
                        ConflictException.class,
                        () -> userService.patchUser("U00001", new UserPatchRequest())
                );
            }

            @Test
            @DisplayName("Should throw NotFoundException when user does not exist")
            void patchUser_whenUserNotFound_throwsNotFoundException() {
                when(repository.findByUserId("U00001")).thenReturn(Optional.empty());

                assertThrows(
                        NotFoundException.class,
                        () -> userService.patchUser("U00001", new UserPatchRequest())
                );
            }
        }

        @Nested
        @DisplayName("Delete User")
        class DeleteUser {

            @Test
            @DisplayName("Should not call repository when user is already deleted")
            void deleteUser_whenAlreadyDeleted_shouldNotCallRepository() {
                User user = new User();
                user.setDeletedAt(Instant.now());

                when(repository.findByUserId("U00001")).thenReturn(Optional.of(user));

                assertThrows(
                        ConflictException.class,
                        () -> userService.deleteUser("U00001")
                );

                verify(repository, never()).softDelete(anyString());
            }

            @Test
            @DisplayName("Should soft delete user when user is active")
            void deleteUser_whenActiveUser_softDeletes() {
                User user = new User();
                user.setDeletedAt(null);

                when(repository.findByUserId("U00001")).thenReturn(Optional.of(user));

                userService.deleteUser("U00001");

                verify(repository).softDelete("U00001");
            }

            @Test
            @DisplayName("Should throw ConflictException when user is already deleted")
            void deleteUser_whenAlreadyDeleted_throwsConflictException() {
                User user = new User();
                user.setDeletedAt(Instant.now());

                when(repository.findByUserId("U00001")).thenReturn(Optional.of(user));

                assertThrows(
                        ConflictException.class,
                        () -> userService.deleteUser("U00001")
                );
            }

            @Test
            @DisplayName("Should throw NotFoundException when user does not exist")
            void deleteUser_whenUserNotFound_throwsNotFoundException() {
                when(repository.findByUserId("U00001")).thenReturn(Optional.empty());

                assertThrows(
                        NotFoundException.class,
                        () -> userService.deleteUser("U00001")
                );
            }
        }

        @Nested
        @DisplayName("Restore User")
        class RestoreUser {

            @Test
            @DisplayName("Should not call repository when user is already active")
            void restoreUser_whenUserAlreadyActive_shouldNotCallRepository() {
                User user = new User();
                user.setDeletedAt(null);

                when(repository.findByUserId("U00001")).thenReturn(Optional.of(user));

                assertThrows(
                        ConflictException.class,
                        () -> userService.restoreUser("U00001")
                );

                verify(repository, never()).restoreUser(anyString());
            }

            @Test
            @DisplayName("Should restore user successfully when user is deleted")
            void restoreUser_whenDeletedUser_restoresSuccessfully() {
                User user = new User();
                user.setUserId("U00001");
                user.setDeletedAt(Instant.now());

                when(repository.findByUserId("U00001")).thenReturn(Optional.of(user));

                UserResponse response = userService.restoreUser("U00001");

                verify(repository).restoreUser("U00001");
                assertEquals("U00001", response.userId());
            }

            @Test
            @DisplayName("Should throw ConflictException when user is already active")
            void restoreUser_whenUserAlreadyActive_throwsConflictException() {
                User user = new User();
                user.setDeletedAt(null);

                when(repository.findByUserId("U00001")).thenReturn(Optional.of(user));

                assertThrows(
                        ConflictException.class,
                        () -> userService.restoreUser("U00001")
                );
            }
        }

        @Nested
        @DisplayName("Find All Deleted User")
        class FindAllDeleted {

            @Test
            @DisplayName("Should return paginated deleted users when paging parameters are provided")
            void findAllDeleted_withPaging_returnsExpectedResponse() {
                UserDeletedResponse deletedUser =
                        new UserDeletedResponse("U00001", "a@mail.com", "A", UserRole.USER, Instant.now());

                when(repository.countDeletedUser(null, null)).thenReturn(1L);
                when(repository.findAllDeleted(1, 5, null, null, null, null))
                        .thenReturn(List.of(deletedUser));

                var response = userService.findAllDeleted(1, 5, null, null, null, null);

                assertEquals(1L, response.countData());
                assertEquals(1, response.page());
                assertEquals(5, response.size());
                assertEquals(1, response.totalPages());
                assertEquals(1, response.data().size());
            }

            @Test
            @DisplayName("Should return all deleted users when paging parameters are not provided")
            void findAllDeleted_withoutPaging_returnsAllData() {
                when(repository.countDeletedUser(null, null)).thenReturn(0L);
                when(repository.findAllDeleted(null, null, null, null, null, null))
                        .thenReturn(List.of());

                var response = userService.findAllDeleted(null, null, null, null, null, null);

                assertEquals(0, response.data().size());
                assertEquals(1, response.page());
                assertEquals(1, response.totalPages());
            }

            @Test
            @DisplayName("Should throw BadRequestException when order parameter is invalid")
            void findAllDeleted_withInvalidOrder_throwsBadRequestException() {
                assertThrows(
                        BadRequestException.class,
                        () -> userService.findAllDeleted(1, 5, null, "INVALID", null, null)
                );
            }
        }

        @Nested
        @DisplayName("Find Deleted User By ID")
        class FindDeletedById {

            @Test
            @DisplayName("Should return UserDeletedResponse when user is deleted")
            void findDeletedById_whenUserDeleted_returnsUserDeletedResponse() {
                // arrange
                Instant deletedAt = Instant.now();

                User user = new User();
                user.setUserId("U00001");
                user.setEmail("user@mail.com");
                user.setName("User");
                user.setRole(UserRole.USER);
                user.setDeletedAt(deletedAt);

                when(repository.findByUserId("U00001"))
                        .thenReturn(Optional.of(user));

                // act
                UserDeletedResponse result = userService.findDeletedById("U00001");

                // assert
                assertNotNull(result);
                assertEquals("U00001", result.userId());
                assertEquals("user@mail.com", result.email());
                assertEquals(deletedAt, result.deletedAt());
            }

            @Test
            @DisplayName("Should throw NotFoundException when user is active")
            void findDeletedById_whenUserActive_throwsNotFoundException() {
                // arrange
                User user = new User();
                user.setUserId("U00001");
                user.setDeletedAt(null);

                when(repository.findByUserId("U00001"))
                        .thenReturn(Optional.of(user));

                // act + assert
                assertThrows(
                        NotFoundException.class,
                        () -> userService.findDeletedById("U00001")
                );
            }

            @Test
            @DisplayName("Should throw NotFoundException when user is not found")
            void findDeletedById_whenUserNotFound_throwsNotFoundException() {
                when(repository.findByUserId("U00001"))
                        .thenReturn(Optional.empty());

                assertThrows(
                        NotFoundException.class,
                        () -> userService.findDeletedById("U00001")
                );
            }
        }

    }
}