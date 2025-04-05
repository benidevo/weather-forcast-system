package com.weatherforecast.authservice.grpc;

import com.weatherforecast.authservice.model.AuthToken;

/**
 * Adapter class that provides conversion methods between domain objects and gRPC protocol objects.
 *
 * <p>The adapter methods transform internal domain objects into their corresponding gRPC message
 * formats that are suitable for network transmission.
 *
 * <p>It contains utility methods for converting authentication and registration related domain
 * objects to their gRPC counterparts, helping to isolate domain logic from the transport-specific
 * details of the gRPC implementation.
 */
public class Adapter {

  /**
   * Converts an AuthToken object to its gRPC AuthResponse representation. This adapter method
   * facilitates the communication between the application domain model and the gRPC service layer.
   *
   * @param authToken The authentication token object from the application domain
   * @return A gRPC AuthResponse object containing the success status, token string, and expiration
   *     timestamp
   */
  public static AuthResponse toGrpcAuthResponse(AuthToken authToken) {
    var response = AuthResponse.newBuilder().setSuccess(authToken.isSuccess());
    if (authToken.isSuccess()) {
      response.setToken(authToken.getToken()).setExpiresAt(authToken.getExpiresAt().toString());
    }

    return response.build();
  }

  /**
   * Converts a boolean success status to its gRPC RegistrationResponse representation. This adapter
   * method facilitates the communication between the application domain model and the gRPC service
   * layer.
   *
   * @param success The success status of the registration process
   * @return A gRPC RegistrationResponse object containing the success status
   */
  public static RegistrationResponse toGrpcRegistrationResponse(boolean success) {
    return RegistrationResponse.newBuilder().setSuccess(success).build();
  }

  /**
   * Converts a boolean success status to a gRPC UpdateResponse message.
   *
   * @param success Boolean indicating whether the operation was successful
   * @return A gRPC UpdateResponse object with the success field set
   */
  public static UpdateResponse toGrpcUpdateResponse(boolean success) {
    return UpdateResponse.newBuilder().setSuccess(success).build();
  }
}
