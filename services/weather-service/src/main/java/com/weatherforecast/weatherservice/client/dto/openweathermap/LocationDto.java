package com.weatherforecast.weatherservice.client.dto.openweathermap;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocationDto {
  private String name;
  private Double lat;
  private Double lon;
  private String country;
  private String state;
}
