package com.weatherforecast.weatherservice.domain;

import lombok.*;

@Data
@Builder
public class Alert {
  private String name;
  private String description;
  private String startTime;
  private String endTime;
}
