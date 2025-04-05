package com.weatherforecast.authservice.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.weatherforecast.authservice.entity.User;
import com.weatherforecast.authservice.model.AuthToken;
import com.weatherforecast.authservice.repository.UserRepository;
import com.weatherforecast.authservice.service.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private User testUser;
    private AuthToken validAuthToken;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username("testuser")
                .password("encodedPassword")
                .build();

        validAuthToken = AuthToken.builder()
                .success(true)
                .token("jwt-token")
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build();
    }

    @Test
    void authenticate_WhenUserNotFound_ShouldReturnFailedAuthToken() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        AuthToken result = authService.authenticate("unknown", "password");

        assertFalse(result.isSuccess());
        assertNull(result.getToken());
        verify(userRepository).findByUsername("unknown");
        verifyNoInteractions(jwtService);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void authenticate_WhenInvalidPassword_ShouldReturnFailedAuthToken() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        AuthToken result = authService.authenticate("testuser", "wrongpassword");

        assertFalse(result.isSuccess());
        assertNull(result.getToken());
        verify(userRepository).findByUsername("testuser");
        verifyNoInteractions(jwtService);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void authenticate_WhenValidCredentials_ShouldReturnAuthTokenAndUpdateLastLogin() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken(testUser)).thenReturn(validAuthToken);

        AuthToken result = authService.authenticate("testuser", "password");

        assertTrue(result.isSuccess());
        assertEquals("jwt-token", result.getToken());

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertNotNull(savedUser.getLastLogin());
        assertTrue(savedUser.getLastLogin().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(savedUser.getLastLogin().isAfter(LocalDateTime.now().minusMinutes(1)));
    }

    @Test
    void register_WhenUsernameExists_ShouldReturnFalse() {
        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(testUser));

        boolean result = authService.register("existinguser", "password");

        assertFalse(result);
        verify(userRepository).findByUsername("existinguser");
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void register_WhenValidRegistration_ShouldSaveUserAndReturnTrue() {
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        boolean result = authService.register("newuser", "password");

        assertTrue(result);
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("newuser", savedUser.getUsername());
        assertEquals("encodedPassword", savedUser.getPassword());
    }

    @Test
    void updatePassword_WhenUserNotFound_ShouldReturnFalse() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        boolean result = authService.updatePassword("unknown", "oldpassword", "newpassword");

        assertFalse(result);
        verify(userRepository).findByUsername("unknown");
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void updatePassword_WhenInvalidOldPassword_ShouldReturnFalse() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        boolean result = authService.updatePassword("testuser", "wrongpassword", "newpassword");

        assertFalse(result);
        verify(userRepository).findByUsername("testuser");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updatePassword_WhenValidCredentials_ShouldUpdatePasswordAndReturnTrue() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldpassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newpassword")).thenReturn("newEncodedPassword");

        boolean result = authService.updatePassword("testuser", "oldpassword", "newpassword");

        assertTrue(result);
        verify(passwordEncoder).encode("newpassword");
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("testuser", savedUser.getUsername());
        assertEquals("newEncodedPassword", savedUser.getPassword());
    }

    @Test
    void updateUsername_WhenNewUsernameExists_ShouldReturnFalse() {
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.of(User.builder().build()));

        boolean result = authService.updateUsername("olduser", "newuser");

        assertFalse(result);
        verify(userRepository).findByUsername("newuser");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUsername_WhenUserNotFound_ShouldReturnFalse() {
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("olduser")).thenReturn(Optional.empty());

        boolean result = authService.updateUsername("olduser", "newuser");

        assertFalse(result);
        verify(userRepository).findByUsername("newuser");
        verify(userRepository).findByUsername("olduser");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUsername_WhenNewUsernameSameAsOld_ShouldReturnFalse() {
        when(userRepository.findByUsername("olduser")).thenReturn(Optional.of(
                User.builder().username("olduser").build()));
        when(userRepository.findByUsername("olduser")).thenReturn(Optional.empty());

        boolean result = authService.updateUsername("olduser", "olduser");

        assertFalse(result);
    }

    @Test
    void updateUsername_WhenValidUpdate_ShouldUpdateUsernameAndReturnTrue() {
        User oldUser = User.builder().username("olduser").build();
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("olduser")).thenReturn(Optional.of(oldUser));

        boolean result = authService.updateUsername("olduser", "newuser");

        assertTrue(result);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("newuser", savedUser.getUsername());
    }
}