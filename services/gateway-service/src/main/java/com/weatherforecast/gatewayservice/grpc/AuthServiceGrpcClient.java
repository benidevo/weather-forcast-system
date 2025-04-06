package com.weatherforecast.gatewayservice.grpc;

import com.weatherforecast.gatewayservice.dto.grpc.AuthDataDto;
import reactor.core.publisher.Mono;

public interface AuthServiceGrpcClient {

  /**
   * Authenticates a user by sending credentials to the authentication service via gRPC.
   *
   * @param username the user identifier used for authentication
   * @param password the user's password for verification
   * @return AuthDataDto containing authentication data such as token and expiration time
   */
  AuthDataDto login(String username, String password);

  /**
   * Registers a new user by sending credentials to the authentication service via gRPC.
   *
   * @param username the user identifier for registration
   * @param password the user's password for registration
   * @return boolean indicating success or failure of the registration process
   */
  boolean register(String username, String password);

  /**
   * Validates a JWT token by sending it to the authentication service via gRPC.
   *
   * @param token the JWT token to be validated
   * @return Mono<Boolean> indicating whether the token is valid or not
   */
  Mono<Boolean> isAuthenticated(String token);
}
