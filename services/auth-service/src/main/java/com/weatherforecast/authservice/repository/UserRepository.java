package com.weatherforecast.authservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weatherforecast.authservice.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Find a user by username.
     *
     * @param username the username of the user
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<User> findByUsername(String username);
}
