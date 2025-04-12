package com.weatherforecast.gatewayservice.dto.http;

public class LoginRequestDto extends AuthCredentialsDto {
  public LoginRequestDto(String username, String password) {
    super(username, password);
  }
}
