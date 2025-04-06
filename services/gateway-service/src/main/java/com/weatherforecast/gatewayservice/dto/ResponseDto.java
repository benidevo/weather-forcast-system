package com.weatherforecast.gatewayservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseDto {
  @Builder.Default private boolean success = true;
  private String message;
  private WeatherDataDto data;
}
