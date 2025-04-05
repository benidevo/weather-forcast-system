package com.weatherforecast.authservice.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import com.weatherforecast.authservice.entity.User;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.weatherforecast.authservice.model.AuthToken;
import com.weatherforecast.authservice.repository.UserRepository;
import com.weatherforecast.authservice.service.AuthService;
import com.weatherforecast.authservice.service.JwtService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public Optional<AuthToken> authenticate(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            log.warn("User with username {} not found", username);
            return Optional.empty();
        }

        if (!passwordEncoder.matches(password, user.get().getPassword())) {
            log.warn("Invalid password for user with username {}", username);
            return Optional.empty();
        }
        var validatedUser = user.get();
        AuthToken authToken = jwtService.generateToken(validatedUser);
        validatedUser.setLastLogin(LocalDateTime.now());
        userRepository.save(validatedUser);

        log.info("User with username {} authenticated successfully", username);
        return Optional.of(authToken);
    }

    @Override
    public boolean register(String username, String password) {
        if (usernameExists(username)) {
            log.warn("Username {} already exists", username);
            return false;
        }

        String encodedPassword = passwordEncoder.encode(password);
        var newUser = User.builder()
                .username(username)
                .password(encodedPassword)
                .build();

        userRepository.save(newUser);
        log.info("User with username {} registered successfully", username);
        return true;
    }

    @Override
    public boolean updatePassword(String username, String oldPassword, String newPassword) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty() || !passwordEncoder.matches(oldPassword, user.get().getPassword())) {
            log.warn("User with username {} not found or old password is incorrect", username);
            return false;
        }

        User updatedUser = user.get();
        updatedUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(updatedUser);
        log.info("Password updated for user with username {}", username);
        return true;
    }

    @Override
    public boolean updateUsername(String oldUsername, String newUsername) {
        if (usernameExists(newUsername)) {
            log.warn("Username {} already exists", newUsername);
            return false;
        }

        Optional<User> user = userRepository.findByUsername(oldUsername);
        if (user.isEmpty() || user.get().getUsername().equals(newUsername)) {
            log.warn("User with username {} not found or new username is same as old", oldUsername);
            return false;
        }

        User updatedUser = user.get();
        updatedUser.setUsername(newUsername);
        userRepository.save(updatedUser);
        log.info("Username updated from {} to {}", oldUsername, newUsername);
        return true;
    }

    private boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}
