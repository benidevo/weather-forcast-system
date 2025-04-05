package com.weatherforecast.authservice.grpc;

import org.lognet.springboot.grpc.GRpcService;

import com.weatherforecast.authservice.model.AuthToken;
import com.weatherforecast.authservice.service.AuthService;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

@GRpcService
@Slf4j
public class AuthServiceGrpcImpl extends AuthServiceGrpc.AuthServiceImplBase {
    private final AuthService authService;

    public AuthServiceGrpcImpl(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void login(LoginRequest request, StreamObserver<AuthResponse> responseObserver) {
        String username = request.getUsername();
        String password = request.getPassword();

        try {
            AuthToken authToken = authService.authenticate(username, password);
            if (!authToken.isSuccess()) {
                log.warn("Authentication failed for user with username {}", username);
                responseObserver.onError(
                        Status.UNAUTHENTICATED.withDescription("Invalid credentials").asRuntimeException());
                return;
            }

            AuthResponse response = Adapter.toGrpcAuthResponse(authToken);
            responseObserver.onNext(response);
            log.info("User with username {} authenticated successfully", username);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error during authentication for user with username {}: {}", username, e.getMessage());
            responseObserver.onError(
                    Status.INTERNAL.withDescription("Something went wrong").asRuntimeException());
        }
    }

    @Override
    public void register(RegisterRequest request, StreamObserver<RegistrationResponse> responseObserver) {
        String username = request.getUsername();
        String password = request.getPassword();

        try {
            boolean registrationSuccess = authService.register(username, password);
            if (!registrationSuccess) {
                log.warn("Registration failed for user with username {}", username);
                responseObserver.onError(
                        Status.ALREADY_EXISTS.withDescription("Username already exists").asRuntimeException());
                return;
            }

            RegistrationResponse response = Adapter.toGrpcRegistrationResponse(registrationSuccess);
            responseObserver.onNext(response);
            log.info("User with username {} registered successfully", username);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error during registration for user with username {}: {}", username, e.getMessage());
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Something went wrong")
                    .asRuntimeException());
        }
    }

    @Override
    public void updatePassword(UpdatePasswordRequest request, StreamObserver<UpdateResponse> responseObserver) {
        String username = request.getUsername();
        String oldPassword = request.getOldPassword();
        String newPassword = request.getNewPassword();

        try {
            boolean updateSuccess = authService.updatePassword(username, oldPassword, newPassword);
            if (!updateSuccess) {
                log.warn("Password update failed for user with username {}", username);
                responseObserver.onError(
                        Status.PERMISSION_DENIED.withDescription("Invalid credentials").asRuntimeException());
                return;
            }

            UpdateResponse response = Adapter.toGrpcUpdateResponse(updateSuccess);
            responseObserver.onNext(response);
            log.info("Password updated successfully for user with username {}", username);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error during password update for user with username {}: {}", username, e.getMessage());
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Something went wrong")
                    .asRuntimeException());
        }
    }

    @Override
    public void updateUsername(UpdateUsernameRequest request, StreamObserver<UpdateResponse> responseObserver) {
        String oldUsername = request.getOldUsername();
        String newUsername = request.getNewUsername();

        try {
            boolean updateSuccess = authService.updateUsername(oldUsername, newUsername);
            if (!updateSuccess) {
                log.warn("Username update failed for user with username {}", oldUsername);
                responseObserver.onError(
                        Status.PERMISSION_DENIED.withDescription("Invalid credentials").asRuntimeException());
                return;
            }

            UpdateResponse response = Adapter.toGrpcUpdateResponse(updateSuccess);
            responseObserver.onNext(response);
            log.info("Username updated successfully for user with username {}", oldUsername);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error during username update for user with username {}: {}", oldUsername, e.getMessage());
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Something went wrong")
                    .asRuntimeException());
        }
    }
}