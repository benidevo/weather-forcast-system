package com.weatherforecast.gatewayservice.dto.grpc;

import java.util.List;
import java.util.ArrayList;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class WeatherDataDto {
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

  @Value
  @Builder
  public static class Forecast {
    private String description;
    private Double temperature;
    private Double feelsLike;
    private Double pressure;
    private Integer humidity;
    private Double windSpeed;
  }

  @Value
  @Builder
  public static class Alert {
    private String name;
    private String description;
    private String startTime;
    private String endTime;
  }
}
