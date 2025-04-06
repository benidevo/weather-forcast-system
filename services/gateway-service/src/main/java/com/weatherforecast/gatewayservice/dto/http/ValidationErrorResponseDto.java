package com.weatherforecast.gatewayservice.dto.http;

import java.util.HashMap;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidationErrorResponseDto {
  @Builder.Default private boolean success = false;
  @Builder.Default private String message = "Validation error";
  private HashMap<String, String> errors;
}
