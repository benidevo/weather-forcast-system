package com.weatherforecast.gatewayservice.grpc.impl;

import com.weatherforecast.authservice.grpc.AuthResponse;
import com.weatherforecast.authservice.grpc.AuthServiceGrpc;
import com.weatherforecast.authservice.grpc.LoginRequest;
import com.weatherforecast.authservice.grpc.RegisterRequest;
import com.weatherforecast.authservice.grpc.RegistrationResponse;
import com.weatherforecast.authservice.grpc.TokenValidationRequest;
import com.weatherforecast.authservice.grpc.TokenValidationResponse;
import com.weatherforecast.gatewayservice.dto.grpc.AuthDataDto;
import com.weatherforecast.gatewayservice.grpc.AuthServiceGrpcClient;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class AuthServiceGrpcClientImpl implements AuthServiceGrpcClient {
  private final ManagedChannel channel;
  private final AuthServiceGrpc.AuthServiceBlockingStub stub;
  private final AuthServiceGrpc.AuthServiceStub asyncStub;
  private static final int channelTerminationTimeout = 5;

  public AuthServiceGrpcClientImpl(
      @Value("${grpc.client.auth-service.address}") String address, CircuitBreaker circuitBreaker) {
    this.channel = ManagedChannelBuilder.forTarget(address).usePlaintext().build();
    this.stub = AuthServiceGrpc.newBlockingStub(channel);
    this.asyncStub = AuthServiceGrpc.newStub(channel);
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

  @Override
  public Mono<Boolean> isAuthenticated(String token) {
    log.info("Sending authentication check request to auth service for token: {}", token);

    return Mono.create(
        sink -> {
          TokenValidationRequest request =
              TokenValidationRequest.newBuilder().setToken(token).build();

          asyncStub
              .withDeadlineAfter(channelTerminationTimeout, TimeUnit.SECONDS)
              .validateToken(
                  request,
                  new StreamObserver<TokenValidationResponse>() {
                    @Override
                    public void onNext(TokenValidationResponse response) {
                      log.info("Authentication check processed for token: {}", token);
                      sink.success(response.getValid());
                    }

                    @Override
                    public void onError(Throwable t) {
                      log.warn(
                          "Authentication check failed for token: {}, error: {}",
                          token,
                          t.getMessage());
                      sink.success(false);
                    }

                    @Override
                    public void onCompleted() {
                      log.info("Completed authentication check for token: {}", token);
                    }
                  });
        });
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
