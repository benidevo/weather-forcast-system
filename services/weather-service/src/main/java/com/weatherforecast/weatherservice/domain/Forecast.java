package com.weatherforecast.weatherservice.domain;

import lombok.*;

@Data
@Builder
public class Forecast {
  private String description;
  private Double temperature;
  private Double feelsLike;
  private Double pressure;
  private Integer humidity;
  private Double windSpeed;
}
