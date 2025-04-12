package com.weatherforecast.gatewayservice.dto.http;


public class RegisterRequestDto extends AuthCredentialsDto {
    public RegisterRequestDto(String username, String password) {
        super(username, password);
    }
}
