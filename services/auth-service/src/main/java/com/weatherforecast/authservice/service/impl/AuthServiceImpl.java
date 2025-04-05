package com.weatherforecast.authservice.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.weatherforecast.authservice.model.AuthToken;
import com.weatherforecast.authservice.repository.UserRepository;
import com.weatherforecast.authservice.service.AuthService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    private UserRepository userRepository;

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<AuthToken> authenticate(String username, String password) {
        return Optional.empty();
    }

    @Override
    public boolean register(String username, String password) {
        return false;
    }

    @Override
    public boolean updatePassword(String username, String oldPassword, String newPassword) {
        return false;
    }

    @Override
    public boolean updateUsername(String oldUsername, String newUsername) {
        return false;
    }
}
