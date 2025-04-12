package com.weatherforecast.gatewayservice.controllers;

import com.weatherforecast.gatewayservice.dto.http.LoginRequestDto;
import com.weatherforecast.gatewayservice.dto.http.LoginResponseDto;
import com.weatherforecast.gatewayservice.dto.http.RegisterRequestDto;
import com.weatherforecast.gatewayservice.dto.http.RegisterResponseDto;
import com.weatherforecast.gatewayservice.grpc.AuthServiceGrpcClient;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
  private final AuthServiceGrpcClient authServiceGrpcClient;

  public AuthController(AuthServiceGrpcClient service) {
    this.authServiceGrpcClient = service;
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponseDto> login(
      @Valid @RequestBody LoginRequestDto loginRequestDto) {
    String password = loginRequestDto.getPassword();
    String email = loginRequestDto.getUsername();
    log.info("Login request received for email: {}", email);
    log.info(password, email);

    var authData = authServiceGrpcClient.login(email, password);
    if (!authData.isSuccess()) {
      log.error("Login failed for email: {}", email);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(LoginResponseDto.builder().message("Login failed").build());
    }

    LoginResponseDto response =
        LoginResponseDto.builder().message("Login successful").data(authData).build();

    return ResponseEntity.ok().body(response);
  }

  @PostMapping("/register")
  public ResponseEntity<RegisterResponseDto> register(
      @Valid @RequestBody RegisterRequestDto registerRequestDto) {
    String password = registerRequestDto.getPassword();
    String email = registerRequestDto.getUsername();
    log.info("Register request received for email: {}", email);
    log.info(password, email);

    var isSuccess = authServiceGrpcClient.register(email, password);
    if (!isSuccess) {
      log.error("Register failed for email: {}", email);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(RegisterResponseDto.builder().message("User already exists").build());
    }

    RegisterResponseDto response =
        RegisterResponseDto.builder().message("Register successful").build();

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
