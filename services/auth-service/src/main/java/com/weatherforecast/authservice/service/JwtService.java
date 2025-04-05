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
}
