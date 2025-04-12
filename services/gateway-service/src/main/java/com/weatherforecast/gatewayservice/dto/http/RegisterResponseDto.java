package com.weatherforecast.gatewayservice.dto.http;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RegisterResponseDto {
  @Builder.Default private boolean success = true;
  private String message;
}
