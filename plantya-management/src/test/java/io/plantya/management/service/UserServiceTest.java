package io.plantya.management.service;

import io.plantya.management.dto.response.UserResponse;
import io.plantya.management.enums.UserBadRequestMessage;
import io.plantya.management.enums.UserRole;
import io.plantya.management.exception.BadRequestException;
import io.plantya.management.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.List;

class UserServiceTest {

    @Test
    void findAllActive_withValidParameters_returnsExpectedResponse() {
        // Arrange
        var userRepository = Mockito.mock(UserRepository.class);
        var userService = new UserService(userRepository);
        var userList = List.of(
                new UserResponse("U001", "user1@domain.com", "User One", UserRole.USER, Instant.now(), Instant.now()),
                new UserResponse("U002", "user2@domain.com", "User Two", UserRole.USER, Instant.now(), Instant.now())
        );

        Mockito.when(userRepository.countActiveUser(null, null)).thenReturn(2L);
        Mockito.when(userRepository.findAllActive(1, 5, null, null, null, null)).thenReturn(userList);

        // Act
        var response = userService.findAllActive(1, 5, null, null, null, null);

        // Assert
        Assertions.assertEquals(2L, response.countData());
        Assertions.assertEquals(1, response.page());
        Assertions.assertEquals(5, response.size());
        Assertions.assertEquals(1, response.totalPages());
        Assertions.assertEquals(2, response.data().size());
        Assertions.assertEquals("user1@domain.com", response.data().get(0).email());
    }

    @Test
    void findAllActive_withInvalidPagination_throwsBadRequestException() {
        // Arrange
        var userRepository = Mockito.mock(UserRepository.class);
        var userService = new UserService(userRepository);

        // Act & Assert
        BadRequestException thrownException = Assertions.assertThrows(
                BadRequestException.class,
                () -> userService.findAllActive(null, 5, null, null, null, null),
                "Expected incomplete pagination parameters to throw BadRequestException"
        );

        Assertions.assertEquals(UserBadRequestMessage.USER_PAGINATION_PARAMETER_INCOMPLETE.getDefaultDetail(), thrownException.getMessage());

        thrownException = Assertions.assertThrows(
                BadRequestException.class,
                () -> userService.findAllActive(0, 5, null, null, null, null),
                "Expected invalid page number to throw BadRequestException"
        );

        Assertions.assertEquals(UserBadRequestMessage.USER_PAGINATION_PARAMETER_INVALID.getDefaultDetail(), thrownException.getMessage());
    }

    @Test
    void findAllActive_withInvalidOrder_throwsIllegalArgumentException() {
        // Arrange
        var userRepository = Mockito.mock(UserRepository.class);
        var userService = new UserService(userRepository);

        // Act & Assert
        IllegalArgumentException thrownException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> userService.findAllActive(1, 5, null, "INVALID_ORDER", null, null),
                "Expected invalid order parameter to throw IllegalArgumentException"
        );

        Assertions.assertEquals("Invalid order value", thrownException.getMessage());
    }

    @Test
    void findAllActive_withEmptyResult_returnsEmptyList() {
        // Arrange
        var userRepository = Mockito.mock(UserRepository.class);
        var userService = new UserService(userRepository);

        Mockito.when(userRepository.countActiveUser(null, null)).thenReturn(0L);
        Mockito.when(userRepository.findAllActive(1, 5, null, null, null, null)).thenReturn(List.of());

        // Act
        var response = userService.findAllActive(1, 5, null, null, null, null);

        // Assert
        Assertions.assertEquals(0L, response.countData());
        Assertions.assertEquals(1, response.page());
        Assertions.assertEquals(5, response.size());
        Assertions.assertEquals(0, response.totalPages());
        Assertions.assertTrue(response.data().isEmpty());
    }

}