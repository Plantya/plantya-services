package io.plantya.user.service;

import io.plantya.user.common.dto.param.UserParam;
import io.plantya.user.common.dto.request.UserQueryParam;
import io.plantya.user.common.exception.BadRequestException;
import io.plantya.user.common.exception.NotFoundException;
import io.plantya.user.common.exception.message.ErrorMessage;
import io.plantya.user.domain.User;
import io.plantya.user.domain.UserRole;
import io.plantya.user.dto.response.PagedUserResponse;
import io.plantya.user.dto.response.UserDeletedResponse;
import io.plantya.user.dto.response.UserGetResponse;
import io.plantya.user.repository.UserDeletedRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDeletedService Test")
class DeletedUserServiceTest {

    @InjectMocks
    UserDeletedService userDeletedService;

    @Mock
    UserDeletedRepository userDeletedRepository;

    @Nested
    @DisplayName("FindAllDeletedUsers")
    class FindAllDeletedUsers {

        @Test
        @DisplayName("Should return paged deleted users successfully")
        void shouldReturnPagedDeletedUsers() {
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
            user1.setDeletedAt(Instant.now());

            User user2 = new User();
            user2.setUserId("user-2");
            user2.setEmail("user2@mail.com");
            user2.setName("User Two");
            user2.setRole(UserRole.USER);
            user2.setDeletedAt(Instant.now());

            List<User> users = List.of(user1, user2);

            when(userDeletedRepository.findAllDeletedUsers(any(UserParam.class)))
                    .thenReturn(users);

            when(userDeletedRepository.countDeletedUsers(any(UserParam.class)))
                    .thenReturn(2L);

            // Act
            PagedUserResponse<UserDeletedResponse> response =
                    userDeletedService.findAllDeletedUsers(param);

            // Assert
            assertNotNull(response);
            assertEquals(2, response.countData());
            assertEquals(1, response.page());
            assertEquals(10, response.size());
            assertEquals(1, response.totalPages());
            assertEquals(2, response.users().size());

            UserDeletedResponse firstUser = response.users().getFirst();
            assertEquals("user-1", firstUser.userId());
            assertEquals("user1@mail.com", firstUser.email());
            assertEquals("User One", firstUser.name());
            assertEquals(UserRole.ADMIN, firstUser.role());
            assertNotNull(firstUser.deletedAt());

            verify(userDeletedRepository).findAllDeletedUsers(any(UserParam.class));
            verify(userDeletedRepository).countDeletedUsers(any(UserParam.class));
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
                    () -> userDeletedService.findAllDeletedUsers(param)
            );

            assertEquals(
                    ErrorMessage.PAGE_LOWER_THAN_ONE.getDefaultDetail(),
                    exception.getDetail()
            );

            verifyNoInteractions(userDeletedRepository);
        }

        @Test
        @DisplayName("Should return empty users when no deleted users found")
        void shouldReturnEmptyListWhenNoData() {
            // Arrange
            UserQueryParam param = new UserQueryParam();
            param.setPage(1);
            param.setSize(10);

            when(userDeletedRepository.findAllDeletedUsers(any(UserParam.class)))
                    .thenReturn(List.of());

            when(userDeletedRepository.countDeletedUsers(any(UserParam.class)))
                    .thenReturn(0L);

            // Act
            PagedUserResponse<UserDeletedResponse> response =
                    userDeletedService.findAllDeletedUsers(param);

            // Assert
            assertNotNull(response);
            assertEquals(0, response.countData());
            assertEquals(1, response.page());
            assertEquals(10, response.size());
            assertEquals(0, response.totalPages());
            assertTrue(response.users().isEmpty());

            verify(userDeletedRepository).findAllDeletedUsers(any(UserParam.class));
            verify(userDeletedRepository).countDeletedUsers(any(UserParam.class));
        }
    }

    @Nested
    @DisplayName("FindDeletedByUserId")
    class FindDeletedByUserId {

        @Test
        @DisplayName("Should return deleted user when user exists and is deleted")
        void shouldReturnDeletedUserWhenExists() {
            // Arrange
            String userId = "user-123";

            User user = new User();
            user.setUserId(userId);
            user.setEmail("user@mail.com");
            user.setName("Deleted User");
            user.setRole(UserRole.ADMIN);
            user.setDeletedAt(Instant.now());

            when(userDeletedRepository.findDeletedByUserId(userId))
                    .thenReturn(user);

            // Act
            UserDeletedResponse response =
                    userDeletedService.findDeletedByUserId(userId);

            // Assert
            assertNotNull(response);
            assertEquals(userId, response.userId());
            assertEquals("user@mail.com", response.email());
            assertEquals("Deleted User", response.name());
            assertEquals(UserRole.ADMIN, response.role());
            assertNotNull(response.deletedAt());

            verify(userDeletedRepository).findDeletedByUserId(userId);
        }

        @Test
        @DisplayName("Should throw NotFoundException when deleted user not found")
        void shouldThrowNotFoundWhenUserNotFound() {
            // Arrange
            String userId = "user-not-found";

            when(userDeletedRepository.findDeletedByUserId(userId))
                    .thenReturn(null);

            // Act & Assert
            NotFoundException exception = assertThrows(
                    NotFoundException.class,
                    () -> userDeletedService.findDeletedByUserId(userId)
            );

            assertEquals(
                    ErrorMessage.USER_NOT_FOUND.getDefaultDetail(),
                    exception.getDetail()
            );

            verify(userDeletedRepository).findDeletedByUserId(userId);
        }
    }

    @Nested
    @DisplayName("RestoreUser")
    class RestoreUser {

        @Test
        @DisplayName("Should restore deleted user successfully")
        void shouldRestoreDeletedUserSuccessfully() {
            // Arrange
            String userId = "user-restore-1";

            User user = new User();
            user.setUserId(userId);
            user.setEmail("restore@mail.com");
            user.setName("Restore User");
            user.setRole(UserRole.USER);
            user.setDeletedAt(Instant.now());
            user.setCreatedAt(Instant.now().minusSeconds(3600));
            user.setUpdatedAt(Instant.now());

            when(userDeletedRepository.findDeletedByUserId(userId))
                    .thenReturn(user);

            // Act
            UserGetResponse response =
                    userDeletedService.restoreUser(userId);

            // Assert
            assertNotNull(response);
            assertEquals(userId, response.userId());
            assertEquals("restore@mail.com", response.email());
            assertEquals("Restore User", response.name());
            assertEquals(UserRole.USER, response.role());
            assertNotNull(response.createdAt());
            assertNotNull(response.updatedAt());

            verify(userDeletedRepository).findDeletedByUserId(userId);
            verify(userDeletedRepository).restoreUser(user);
        }

        @Test
        @DisplayName("Should throw NotFoundException when user not found")
        void shouldThrowNotFoundWhenUserNotFound() {
            // Arrange
            String userId = "user-not-found";

            when(userDeletedRepository.findDeletedByUserId(userId))
                    .thenReturn(null);

            // Act & Assert
            NotFoundException exception = assertThrows(
                    NotFoundException.class,
                    () -> userDeletedService.restoreUser(userId)
            );

            assertEquals(
                    ErrorMessage.USER_NOT_FOUND.getDefaultDetail(),
                    exception.getDetail()
            );

            verify(userDeletedRepository).findDeletedByUserId(userId);
            verify(userDeletedRepository, never()).restoreUser(any());
        }

        @Test
        @DisplayName("Should throw NotFoundException when user is already active")
        void shouldThrowNotFoundWhenUserAlreadyActive() {
            // Arrange
            String userId = "user-active";

            User user = new User();
            user.setUserId(userId);
            user.setDeletedAt(null); // already active

            when(userDeletedRepository.findDeletedByUserId(userId))
                    .thenReturn(user);

            // Act & Assert
            NotFoundException exception = assertThrows(
                    NotFoundException.class,
                    () -> userDeletedService.restoreUser(userId)
            );

            assertEquals(
                    ErrorMessage.USER_NOT_FOUND.getDefaultDetail(),
                    exception.getDetail()
            );

            verify(userDeletedRepository).findDeletedByUserId(userId);
            verify(userDeletedRepository, never()).restoreUser(any());
        }
    }
}