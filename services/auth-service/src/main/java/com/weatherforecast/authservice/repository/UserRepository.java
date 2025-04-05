package com.weatherforecast.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weatherforecast.authservice.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Find a user by username.
     *
     * @param username the username of the user
     * @return the user with the given username, or null if no such user exists
     */
    User findByUsername(String username);
}
