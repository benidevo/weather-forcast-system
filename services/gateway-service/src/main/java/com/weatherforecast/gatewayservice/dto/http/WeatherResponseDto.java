package com.weatherforecast.gatewayservice.dto.http;

import com.weatherforecast.gatewayservice.dto.grpc.WeatherDataDto;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class WeatherResponseDto {
  @Builder.Default private boolean success = true;
  private String message;
  private WeatherDataDto data;
}
