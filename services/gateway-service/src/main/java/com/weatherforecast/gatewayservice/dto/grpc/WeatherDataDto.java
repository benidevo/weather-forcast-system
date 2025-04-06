package com.weatherforecast.gatewayservice.dto.grpc;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
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
  private List<Forecast> forecast;
  private List<Alert> alerts;

  @Data
  @Builder
  public static class Forecast {
    private String description;
    private Double temperature;
    private Double feelsLike;
    private Double pressure;
    private Integer humidity;
    private Double windSpeed;
  }

  @Data
  @Builder
  public static class Alert {
    private String name;
    private String description;
    private String startTime;
    private String endTime;
  }
}
