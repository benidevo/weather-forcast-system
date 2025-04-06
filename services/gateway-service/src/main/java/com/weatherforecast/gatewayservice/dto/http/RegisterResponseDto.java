package com.weatherforecast.gatewayservice.dto.http;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterResponseDto {
  @Builder.Default private boolean success = true;
  private String message;
}
