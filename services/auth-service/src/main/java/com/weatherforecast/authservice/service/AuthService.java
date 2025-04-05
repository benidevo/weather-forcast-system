package com.weatherforecast.authservice.service;

import java.util.Optional;

import com.weatherforecast.authservice.model.AuthToken;

public interface AuthService {
    /**
     * Authenticate a user with the given username and password.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return an AuthToken containing the authentication result and token if
     *         successful
     */
    AuthToken authenticate(String username, String password);

    /**
     * Register a new user with the given username and password.
     *
     * @param username the username of the new user
     * @param password the password of the new user
     * @return true if registration is successful, false otherwise
     */
    boolean register(String username, String password);

    /**
     * Change the password of an existing user.
     *
     * @param username    the username of the user
     * @param oldPassword the current password of the user
     * @param newPassword the new password for the user
     * @return true if the password change is successful, false otherwise
     */
    boolean updatePassword(String username, String oldPassword, String newPassword);

    /**
     * Update the username of an existing user.
     *
     * @param oldUsername the current username of the user
     * @param newUsername the new username for the user
     * @return true if the username update is successful, false otherwise
     */
    boolean updateUsername(String oldUsername, String newUsername);

}
