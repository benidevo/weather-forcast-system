package com.weatherforecast.authservice.service;

import com.weatherforecast.authservice.entity.User;
import com.weatherforecast.authservice.model.AuthToken;

public interface JwtService {
  /**
   * Generates a JWT token for the given user.
   *
   * @param user the user for whom to generate the token
   * @return the generated JWT token
   */
  public AuthToken generateToken(User user);

  /**
   * Validates the given JWT token.
   *
   * @param token the JWT token to validate
   * @return true if the token is valid, false otherwise
   */
  public boolean validateToken(String token);
}
