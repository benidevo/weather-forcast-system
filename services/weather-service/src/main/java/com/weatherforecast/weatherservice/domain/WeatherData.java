package com.weatherforecast.weatherservice.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class WeatherData {
  private Double latitude;
  private Double longitude;
  private String timezone;
  private String timezoneOffset;
  private String description;
  private Double temperature;
  private Double feelsLike;
  private Double pressure;
  private Integer humidity;
  private Double windSpeed;
  @Builder.Default
  private List<Forecast> forecast = new ArrayList<>();
  @Builder.Default
  private List<Alert> alerts = new ArrayList<>();
}
