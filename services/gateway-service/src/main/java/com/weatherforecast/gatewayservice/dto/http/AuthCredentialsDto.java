package com.weatherforecast.gatewayservice.dto.http;

import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;


@SuperBuilder
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AuthCredentialsDto {
  @EqualsAndHashCode.Include
  @NotBlank(message = "Username is required")
  private String username;

  @ToString.Exclude
  @NotBlank(message = "Password is required")
  private String password;

  public AuthCredentialsDto(String username, String password) {
    this.username = username;
    this.password = password;
  }
}
