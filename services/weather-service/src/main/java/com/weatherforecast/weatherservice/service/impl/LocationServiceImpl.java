package com.weatherforecast.weatherservice.service.impl;

import com.weatherforecast.weatherservice.client.WeatherApiClient;
import com.weatherforecast.weatherservice.domain.Coordinates;
import com.weatherforecast.weatherservice.service.LocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class LocationServiceImpl implements LocationService {
  private final WeatherApiClient weatherApiClient;

  public LocationServiceImpl(WeatherApiClient weatherApiClient) {
    this.weatherApiClient = weatherApiClient;
  }

  @Override
  public Mono<Coordinates> getCoordinates(String location) {
    return weatherApiClient
        .getCoordinates(location)
        .doOnSuccess(
            coordinates -> {
              if (coordinates != null) {
                log.info("Coordinates retrieved: {}", coordinates);
              } else {
                log.info("No coordinates found for location: {}", location);
              }
            })
        .doOnError(
            error ->
                log.error(
                    "Error retrieving coordinates for location: {} Error: {}",
                    location,
                    error.getMessage()));
  }
}
