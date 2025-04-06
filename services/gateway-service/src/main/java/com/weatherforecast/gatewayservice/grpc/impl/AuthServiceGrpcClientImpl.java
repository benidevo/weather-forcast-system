package com.weatherforecast.gatewayservice.grpc.impl;

import com.weatherforecast.authservice.grpc.AuthResponse;
import com.weatherforecast.authservice.grpc.AuthServiceGrpc;
import com.weatherforecast.authservice.grpc.LoginRequest;
import com.weatherforecast.authservice.grpc.RegisterRequest;
import com.weatherforecast.authservice.grpc.RegistrationResponse;
import com.weatherforecast.gatewayservice.dto.grpc.AuthDataDto;
import com.weatherforecast.gatewayservice.grpc.AuthServiceGrpcClient;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthServiceGrpcClientImpl implements AuthServiceGrpcClient {
  private ManagedChannel channel;
  private AuthServiceGrpc.AuthServiceBlockingStub stub;
  private final int channelTerminationTimeout = 5;

  public AuthServiceGrpcClientImpl(
      @Value("${grpc.client.auth-service.address}") String address, CircuitBreaker circuitBreaker) {
    log.info("Creating gRPC channel to auth service at address: {}", address);
    this.channel = ManagedChannelBuilder.forTarget(address).usePlaintext().build();
    this.stub = AuthServiceGrpc.newBlockingStub(channel);
  }

  @PreDestroy
  public void shutdown() {
    log.info("Shutting down auth service gRPC channel");
    if (channel != null && !channel.isShutdown()) {
      try {
        log.info("Waiting for {} seconds for gRPC channel to shut down", channelTerminationTimeout);
        channel.shutdown().awaitTermination(channelTerminationTimeout, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        log.error("Error shutting down gRPC channel", e);
        Thread.currentThread().interrupt();
      } finally {
        if (!channel.isTerminated()) {
          log.warn("gRPC channel did not shut down in time, forcing shutdown");
          channel.shutdownNow();
        }
      }
    }
    log.info("gRPC channel shut down successfully");
  }

  @Override
  public AuthDataDto login(String username, String password) {
    log.info("Sending login request to auth service for username: {}", username);
    try {
      LoginRequest request =
          LoginRequest.newBuilder().setUsername(username).setPassword(password).build();
      AuthResponse response =
          stub.withDeadlineAfter(channelTerminationTimeout, TimeUnit.SECONDS).login(request);

      log.info("Login attempt processed for username: {}", username);
      return mapGrpcAuthResponseToDto(response);
    } catch (StatusRuntimeException e) {
      log.warn("Authentication failed for username: {}, error: {}", username, e.getMessage());
      return AuthDataDto.builder().success(false).build();
    }
  }

  @Override
  public boolean register(String username, String password) {
    log.info("Sending registration request to auth service for username: {}", username);
    try {
      RegisterRequest request =
          RegisterRequest.newBuilder().setUsername(username).setPassword(password).build();

      RegistrationResponse response =
          stub.withDeadlineAfter(channelTerminationTimeout, TimeUnit.SECONDS).register(request);

      log.info("Registration attempt processed for username: {}", username);
      return response.getSuccess();
    } catch (StatusRuntimeException e) {
      log.warn("Registration failed for username: {}, error: {}", username, e.getMessage());
      return false;
    }
  }

  private AuthDataDto mapGrpcAuthResponseToDto(AuthResponse response) {
    var dto =
        AuthDataDto.builder()
            .success(response.getSuccess())
            .token(response.getToken())
            .expiresAt(response.getExpiresAt());

    if (response.getSuccess()) {
      dto.success(response.getSuccess());
    }

    return dto.build();
  }
}
