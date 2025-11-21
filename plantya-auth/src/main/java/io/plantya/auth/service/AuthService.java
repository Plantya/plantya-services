package io.plantya.auth.service;

import io.plantya.auth.dto.request.LoginRequest;
import io.plantya.auth.dto.request.RegisterRequest;
import io.plantya.auth.dto.response.AppResponse;
import io.plantya.auth.dto.response.LoginResponse;
import io.plantya.auth.dto.response.LoginResult;
import io.plantya.auth.dto.response.RegisterResponse;
import io.plantya.auth.exception.BadRequestException;
import io.plantya.auth.exception.InternalServerErrorException;
import io.plantya.auth.exception.InvalidCredentialsException;
import io.plantya.auth.exception.RegistrationException;
import io.plantya.auth.repository.AuthRepository;
import io.plantya.auth.logging.AppLogger;
import io.plantya.auth.util.AuthValidator;
import io.plantya.auth.util.PasswordUtil;
import jakarta.enterprise.context.ApplicationScoped;
import io.plantya.auth.entity.User;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

@ApplicationScoped
public class AuthService {

    private final AuthRepository repository;

    public AuthService(AuthRepository repository) {
        this.repository = repository;
    }

    public LoginResult login(LoginRequest request) {
        boolean isLoggedInByEmail = false;

        if (request == null) {
            throw new BadRequestException("Invalid login data");
        }

        AppLogger.start("User '" + request.usernameOrEmail() + "' attempted to login", request, request.password().length());

        User user;
        try {
            AuthValidator.login(request);

            if (AuthValidator.EMAIL_PATTERN.matcher(request.usernameOrEmail()).matches()) {
                user = repository.findOneByEmail(request.usernameOrEmail());
                isLoggedInByEmail = true;
            } else {
                user = repository.findOneByUsername(request.usernameOrEmail());
            }

            if (user == null) {
                throw new InvalidCredentialsException("User not found");
            }

            boolean isValidPassword = PasswordUtil.verify(request.password(), user.getPassword());
            if (!isValidPassword) {
                throw new InvalidCredentialsException("Invalid password");
            }

            if (isLoggedInByEmail && !user.getEmail().equals(request.usernameOrEmail())) {
                throw new InvalidCredentialsException(
                        "Email mismatch. Given:  " + request.usernameOrEmail() + ". Found:  " + user.getEmail()
                );
            } else if (!isLoggedInByEmail && !user.getUsername().equals(request.usernameOrEmail())) {
                throw new InvalidCredentialsException(
                        "Username mismatch. Given:  " + request.usernameOrEmail() + ". Found:  " + user.getUsername()
                );
            }
        } catch (InvalidCredentialsException | BadRequestException e) {
            AppLogger.error("Login failed: " + e.getMessage(), e);
            AppLogger.end("User '" + request.usernameOrEmail() + "' login failed", "", 0);

            throw new BadRequestException("Invalid username or password");
        } catch (Exception e) {
            AppLogger.error("Unexpected error during login: " + e.getMessage(), e);
            AppLogger.end("User '" + request.usernameOrEmail() + "' login failed", "null", 0);

            throw new InternalServerErrorException("Unexpected server error");
        }

        LoginResponse response = new LoginResponse(user.getUsername(), user.getEmail(), user.getRole().toString());
        AppLogger.end("User '" + request.usernameOrEmail() + "' login complete", response, request.password().length());

        return new LoginResult(
                new AppResponse<>("user " + user.getUsername() + " logged in", response, LocalDateTime.now()),
                user
        );
    }

    @Transactional
    public AppResponse<RegisterResponse> register(RegisterRequest request) {
        if (request == null) {
            throw new BadRequestException("Invalid registration data");
        }

        AppLogger.start("User '" + request.username() + "' attempted to register", request, request.password().length());

        User user;
        try {
            AuthValidator.register(request);

            if (repository.existsByUsername(request.username())) {
                throw new RegistrationException("Username already exists");
            }

            if (repository.existsByEmail(request.email())) {
                throw new RegistrationException("Email already exists");
            }

            user = new User(request.username(), request.email(), PasswordUtil.hash(request.password()));
            repository.persist(user);
        } catch (InvalidCredentialsException | BadRequestException e) {
            AppLogger.error("Registration failed: " + e.getMessage(), e);
            AppLogger.end("User '" + request.username() + "' registration failed", "", 0);

            throw new BadRequestException(e.getMessage());
        } catch (RegistrationException e) {
            AppLogger.error("Registration failed: " + e.getMessage(), e);
            AppLogger.end("User '" + request.username() + "' registration failed", "", 0);

            throw new BadRequestException("Invalid registration data");
        } catch (Exception e) {
            AppLogger.error("Unexpected error during registration: " + e.getMessage(), e);
            AppLogger.end("User '" + request.username() + "' registration failed", "", 0);

            throw new InternalServerErrorException("Unexpected server error");
        }

        RegisterResponse response = new RegisterResponse(
                user.getUsername(),
                user.getEmail(),
                user.getRole().toString(),
                user.getCreatedAt()
        );
        AppLogger.end("User '" + request.username() + "' registration complete", response, request.password().length());

        return new AppResponse<>("user " + user.getUsername() + " registered", response, LocalDateTime.now());
    }

    public AppResponse<String> logout() {
        AppLogger.info("User logged out");
        return new AppResponse<>("Logged out successfully", null, LocalDateTime.now());
    }

}
