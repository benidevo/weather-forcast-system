package com.weatherforecast.weatherservice.domain;

import java.util.List;
import lombok.*;

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
  private List<Forecast> forecast;
  private List<Alert> alerts;
}
