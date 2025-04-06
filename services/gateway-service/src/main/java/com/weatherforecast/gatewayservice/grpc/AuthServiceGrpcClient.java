package com.weatherforecast.gatewayservice.grpc;

import com.weatherforecast.gatewayservice.dto.grpc.AuthDataDto;

public interface AuthServiceGrpcClient {

  /**
   * Authenticates a user by sending credentials to the authentication service via gRPC.
   *
   * @param username the user identifier used for authentication
   * @param password the user's password for verification
   * @return AuthDataDto containing authentication data such as token and expiration time
   */
  AuthDataDto login(String username, String password);

  boolean register(String username, String password);
}
