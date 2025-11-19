package com.agrosentinel.auth.service;

import com.agrosentinel.auth.dto.request.LoginRequest;
import com.agrosentinel.auth.dto.request.RegisterRequest;
import com.agrosentinel.auth.dto.response.AppResponse;
import com.agrosentinel.auth.dto.response.LoginResponse;
import com.agrosentinel.auth.dto.response.LoginResult;
import com.agrosentinel.auth.dto.response.RegisterResponse;
import com.agrosentinel.auth.exception.BadRequestException;
import com.agrosentinel.auth.exception.InternalServerErrorException;
import com.agrosentinel.auth.exception.InvalidCredentialsException;
import com.agrosentinel.auth.exception.RegistrationException;
import com.agrosentinel.auth.repository.AuthRepository;
import com.agrosentinel.auth.logging.AppLogger;
import com.agrosentinel.auth.util.AuthValidator;
import com.agrosentinel.auth.util.PasswordUtil;
import jakarta.enterprise.context.ApplicationScoped;
import com.agrosentinel.auth.entity.User;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Request;

import java.time.LocalDateTime;

@ApplicationScoped
public class AuthService {

    private final AuthRepository repository;
    private final Request request;

    public AuthService(AuthRepository repository, Request request) {
        this.repository = repository;
        this.request = request;
    }

    public LoginResult login(LoginRequest request) {
        if (request == null) {
            throw new BadRequestException("Invalid login data");
        }

        AppLogger.start("User '" + request.username() + "' attempted to login", request, request.password().length());

        User user;
        try {
            AuthValidator.login(request);

            user = repository.findOneByUsername(request.username());
            if (user == null) {
                throw new InvalidCredentialsException("Invalid username or password");
            }

            boolean isValidPassword = PasswordUtil.verify(request.password(), user.getPassword());
            if (!user.getUsername().equals(request.username()) || !isValidPassword) {
                throw new InvalidCredentialsException("Invalid username or password");
            }
        } catch (InvalidCredentialsException | RegistrationException e) {
            AppLogger.error("Login failed: " + e.getMessage(), e);
            AppLogger.end("User '" + request.username() + "' login failed", "", 0);

            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            AppLogger.error("Unexpected error during login: " + e.getMessage(), e);
            AppLogger.end("User '" + request.username() + "' login failed", "null", 0);

            throw new InternalServerErrorException("Unexpected server error");
        }

        LoginResponse response = new LoginResponse(user.getUsername(), user.getEmail(), user.getRole().toString());
        AppLogger.end("User '" + request.username() + "' login complete", response, request.password().length());

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
        } catch (InvalidCredentialsException | RegistrationException e) {
            AppLogger.error("Registration failed: " + e.getMessage(), e);
            AppLogger.end("User '" + request.username() + "' registration failed", "", 0);

            throw new BadRequestException(e.getMessage());
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
