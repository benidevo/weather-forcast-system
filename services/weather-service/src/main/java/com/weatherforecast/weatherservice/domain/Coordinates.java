package com.weatherforecast.weatherservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class Coordinates {
  private Double latitude;
  private Double longitude;
}
