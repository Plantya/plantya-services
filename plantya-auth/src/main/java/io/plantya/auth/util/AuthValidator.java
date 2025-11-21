package io.plantya.auth.util;

import io.plantya.auth.dto.request.LoginRequest;
import io.plantya.auth.dto.request.RegisterRequest;
import io.plantya.auth.exception.InvalidCredentialsException;
import io.plantya.auth.exception.RegistrationException;
import io.quarkus.security.UnauthorizedException;

import java.util.regex.Pattern;

public class AuthValidator {

    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{4,20}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,64}$");

    public static void login(LoginRequest request) throws InvalidCredentialsException {
        if (request.usernameOrEmail() == null || request.password() == null) {
            throw new UnauthorizedException("Invalid username or password");
        }

        if (request.usernameOrEmail().isBlank() || request.password().isBlank()) {
            throw new UnauthorizedException("Invalid username or password");
        }
    }

    public static void register(RegisterRequest request) throws RegistrationException {
        // --- Null checks ---
        if (request == null) {
            throw new RegistrationException("Invalid registration data");
        }

        if (request.username() == null || request.email() == null ||
                request.password() == null || request.confirmPassword() == null) {
            throw new RegistrationException("Invalid registration data");
        }

        // --- Blank checks ---
        if (request.username().isBlank() || request.email().isBlank() ||
                request.password().isBlank() || request.confirmPassword().isBlank()) {
            throw new RegistrationException("Invalid registration data");
        }

        // --- Username ---
        if (!USERNAME_PATTERN.matcher(request.username()).matches()) {
            throw new RegistrationException("Username must be 4–20 characters (letters, numbers, underscores only)");
        }

        // --- Email ---
        if (!EMAIL_PATTERN.matcher(request.email()).matches()) {
            throw new RegistrationException("Invalid email address");
        }

        // --- Password ---
        if (!PASSWORD_PATTERN.matcher(request.password()).matches()) {
            throw new RegistrationException("Password must be 8–64 chars, with upper, lower, and number");
        }

        // --- Password confirmation ---
        if (!request.password().equals(request.confirmPassword())) {
            throw new RegistrationException("Passwords do not match");
        }
    }

}