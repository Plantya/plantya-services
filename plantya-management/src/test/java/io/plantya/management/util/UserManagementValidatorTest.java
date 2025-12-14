package io.plantya.management.util;

import io.plantya.management.dto.request.UserRequest;
import io.plantya.management.enums.UserRole;
import io.plantya.management.exception.BadRequestException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.plantya.management.enums.UserBadRequestMessage.*;

/**
 * Unit tests for {@link UserManagementValidator#validateCreateRequest(UserRequest)}.
 * <p>
 * This test class ensures that the validateCreateRequest method works as expected
 * by covering various scenarios such as null checks, email format validation,
 * and name field requirements.
 */
public class UserManagementValidatorTest {

    @Test
    void testValidateCreateRequest_NullRequest_ThrowsBadRequestException() {
        // Arrange
        UserManagementValidator validator = new UserManagementValidator();

        // Act & Assert
        BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> validator.validateCreateRequest(null)
        );
        Assertions.assertEquals(USER_INVALID_REQUEST_PAYLOAD.getDefaultDetail(), exception.getMessage());
    }

    @Test
    void testValidateCreateRequest_NullEmail_ThrowsBadRequestException() {
        // Arrange
        UserManagementValidator validator = new UserManagementValidator();
        UserRequest request = new UserRequest(null, "John Doe", UserRole.USER, "password123");

        // Act & Assert
        BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> validator.validateCreateRequest(request)
        );
        Assertions.assertEquals(USER_FIELD_REQUIRED.getDefaultDetail(), exception.getMessage());
    }

    @Test
    void testValidateCreateRequest_BlankEmail_ThrowsBadRequestException() {
        // Arrange
        UserManagementValidator validator = new UserManagementValidator();
        UserRequest request = new UserRequest("", "John Doe", UserRole.USER, "password123");

        // Act & Assert
        BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> validator.validateCreateRequest(request)
        );
        Assertions.assertEquals(USER_FIELD_REQUIRED.getDefaultDetail(), exception.getMessage());
    }

    @Test
    void testValidateCreateRequest_InvalidEmailFormat_ThrowsBadRequestException() {
        // Arrange
        UserManagementValidator validator = new UserManagementValidator();
        UserRequest request = new UserRequest("invalid-email", "John Doe", UserRole.USER, "password123");

        // Act & Assert
        BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> validator.validateCreateRequest(request)
        );
        Assertions.assertEquals(USER_INVALID_EMAIL_FORMAT.getDefaultDetail(), exception.getMessage());
    }

    @Test
    void testValidateCreateRequest_NullName_ThrowsBadRequestException() {
        // Arrange
        UserManagementValidator validator = new UserManagementValidator();
        UserRequest request = new UserRequest("john.doe@example.com", null, UserRole.USER, "password123");

        // Act & Assert
        BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> validator.validateCreateRequest(request)
        );
        Assertions.assertEquals(USER_FIELD_REQUIRED.getDefaultDetail(), exception.getMessage());
    }

    @Test
    void testValidateCreateRequest_BlankName_ThrowsBadRequestException() {
        // Arrange
        UserManagementValidator validator = new UserManagementValidator();
        UserRequest request = new UserRequest("john.doe@example.com", "  ", UserRole.USER, "password123");

        // Act & Assert
        BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> validator.validateCreateRequest(request)
        );
        Assertions.assertEquals(USER_FIELD_REQUIRED.getDefaultDetail(), exception.getMessage());
    }

    @Test
    void testValidateCreateRequest_ValidRequest_NoExceptionThrown() {
        // Arrange
        UserManagementValidator validator = new UserManagementValidator();
        UserRequest request = new UserRequest("john.doe@example.com", "John Doe", UserRole.USER, "password123");

        // Act & Assert
        Assertions.assertDoesNotThrow(() -> validator.validateCreateRequest(request));
    }
}