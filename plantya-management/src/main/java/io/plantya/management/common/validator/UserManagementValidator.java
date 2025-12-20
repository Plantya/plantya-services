package io.plantya.management.common.validator;

import io.plantya.management.dto.request.UserPatchRequest;
import io.plantya.management.dto.request.UserRequest;
import io.plantya.management.exception.BadRequestException;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.regex.Pattern;

import static io.plantya.management.enums.UserBadRequestMessage.*;

@ApplicationScoped
public class UserManagementValidator {

    /**
     * Email validation regex based on a simplified RFC-compliant pattern.
     */
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    /**
     * Precompiled email pattern for performance and reuse.
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    /**
     * Validates request payload for user creation.
     *
     * <p>This validation ensures that:
     * <ul>
     *   <li>Request payload is not null</li>
     *   <li>Email is present and has a valid format</li>
     *   <li>Name is present and not blank</li>
     * </ul>
     *
     * @param request user creation request payload
     * @throws BadRequestException if validation fails
     */
    public void validateCreateRequest(UserRequest request) {
        if (request == null) {
            throw new BadRequestException(USER_INVALID_REQUEST_PAYLOAD);
        }

        validateEmail(request.email());

        if (request.name() == null || request.name().isBlank()) {
            throw new BadRequestException(USER_FIELD_REQUIRED);
        }
    }

    /**
     * Validates request payload for partial user update (PATCH).
     *
     * <p>This validation ensures that:
     * <ul>
     *   <li>Request payload is not null</li>
     *   <li>At least one updatable field is provided</li>
     *   <li>All provided fields satisfy their respective constraints</li>
     * </ul>
     *
     * @param request patch user request payload
     * @throws BadRequestException if validation fails
     */
    public void validatePatchRequest(UserPatchRequest request) {
        if (request == null) {
            throw new BadRequestException(USER_INVALID_REQUEST_PAYLOAD);
        }

        if (!hasAnyUpdatableField(request)) {
            throw new BadRequestException(USER_PATCH_EMPTY);
        }

        validatePatchFields(request);
    }

    /**
     * Validates an email address.
     *
     * <p>The email must not be null, blank, and must conform
     * to the expected email format.
     *
     * @param email email address to validate
     * @throws BadRequestException if email is invalid
     */
    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException(USER_FIELD_REQUIRED);
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BadRequestException(USER_INVALID_EMAIL_FORMAT);
        }
    }

    /**
     * Checks whether at least one updatable field is present
     * in the patch request.
     *
     * @param request patch user request payload
     * @return true if at least one field is provided, false otherwise
     */
    private boolean hasAnyUpdatableField(UserPatchRequest request) {
        return request.getEmail() != null ||
                request.getName() != null ||
                request.getRole() != null ||
                request.getPassword() != null;
    }

    /**
     * Validates individual fields provided in a patch request.
     *
     * <p>Only fields that are present (non-null) will be validated.
     *
     * @param request patch user request payload
     * @throws BadRequestException if any provided field is invalid
     */
    private void validatePatchFields(UserPatchRequest request) {
        if (request.getEmail() != null) {
            validateEmail(request.getEmail());
        }

        if (request.getName() != null && request.getName().isBlank()) {
            throw new BadRequestException(USER_INVALID_NAME);
        }

        if (request.getPassword() != null) {
            validatePassword(request.getPassword());
        }

        if (request.getRole() != null && request.getRole().name().isBlank()) {
            throw new BadRequestException(USER_INVALID_ROLE);
        }
    }

    /**
     * Validates a password value.
     *
     * <p>Password must not be blank and must meet
     * the minimum length requirement.
     *
     * @param password password value to validate
     * @throws BadRequestException if the password is invalid
     */
    private void validatePassword(String password) {
        if (password.isBlank() || password.length() < 8) {
            throw new BadRequestException(USER_INVALID_PASSWORD);
        }
    }
}
