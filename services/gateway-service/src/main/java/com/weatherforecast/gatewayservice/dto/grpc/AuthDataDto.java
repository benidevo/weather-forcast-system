package com.weatherforecast.gatewayservice.dto.grpc;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthDataDto {
  @Builder.Default private boolean success = false;
  private String token;
  private String expiresAt;
}
