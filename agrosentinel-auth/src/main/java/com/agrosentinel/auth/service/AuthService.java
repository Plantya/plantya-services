package com.agrosentinel.auth.service;

import com.agrosentinel.auth.model.dto.*;
import com.agrosentinel.auth.model.exception.BadRequestException;
import com.agrosentinel.auth.model.exception.InternalServerErrorException;
import com.agrosentinel.auth.model.exception.InvalidCredentialsException;
import com.agrosentinel.auth.model.exception.RegistrationException;
import com.agrosentinel.auth.repository.AuthRepository;
import com.agrosentinel.auth.util.AppLogger;
import com.agrosentinel.auth.util.AuthValidator;
import com.agrosentinel.auth.util.PasswordUtil;
import io.quarkus.security.UnauthorizedException;
import jakarta.enterprise.context.ApplicationScoped;
import com.agrosentinel.auth.model.entity.User;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

@ApplicationScoped
public class AuthService {

    private final AuthRepository repository;

    public AuthService(AuthRepository repository) {
        this.repository = repository;
    }

    public LoginResult login(LoginRequest request) {
        if (request == null) {
            throw new BadRequestException("Invalid login data");
        }

        AppLogger.start("User '" + request.username() + "' attempted to login");

        User user;
        try {
            AuthValidator.login(request);

            user = repository.findOneByUsername(request.username());
            if (user == null) {
                throw new UnauthorizedException("Invalid username or password");
            }

            boolean isValidPassword = PasswordUtil.verify(request.password(), user.getPassword());
            if (!user.getUsername().equals(request.username()) || !isValidPassword) {
                throw new UnauthorizedException("Invalid username or password");
            }
        } catch (UnauthorizedException | InvalidCredentialsException | RegistrationException e) {
            AppLogger.error("Login failed: " + e.getMessage(), e);
            AppLogger.end("User '" + request.username() + "' login failed");

            throw new BadRequestException("Unable to complete login");
        } catch (Exception e) {
            AppLogger.error("Unexpected error during login: " + e.getMessage(), e);
            AppLogger.end("User '" + request.username() + "' login failed");

            throw new InternalServerErrorException("Unexpected server error");
        }

        AppLogger.end("User '" + request.username() + "' login complete");

        LoginResponse response = new LoginResponse(user.getUsername(), user.getEmail(), user.getRole().toString());
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

        AppLogger.start("User '" + request.username() + "' attempted to register");

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
        } catch (UnauthorizedException | InvalidCredentialsException | RegistrationException e) {
            AppLogger.error("Registration failed: " + e.getMessage(), e);
            AppLogger.end("User '" + request.username() + "' registration failed");

            throw new BadRequestException("Unable to complete registration");
        } catch (Exception e) {
            AppLogger.error("Unexpected error during registration: " + e.getMessage(), e);
            AppLogger.end("User '" + request.username() + "' registration failed");

            throw new InternalServerErrorException("Unexpected server error");
        }

        AppLogger.end("User '" + request.username() + "' registration complete");
        RegisterResponse response = new RegisterResponse(
                user.getUsername(),
                user.getEmail(),
                user.getRole().toString(),
                user.getCreatedAt()
        );

        return new AppResponse<>("user " + user.getUsername() + " registered", response, LocalDateTime.now());
    }

    public AppResponse<String> logout() {
        AppLogger.info("User logged out");
        return new AppResponse<>("Logged out successfully", null, LocalDateTime.now());
    }

}
