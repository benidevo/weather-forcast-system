package com.weatherforecast.weatherservice.client;

import com.weatherforecast.weatherservice.domain.Coordinates;
import com.weatherforecast.weatherservice.domain.WeatherData;
import reactor.core.publisher.Mono;

public interface WeatherApiClient {

  /**
   * Retrieves weather data for a specific geographic location.
   *
   * @param latitude The latitude coordinate of the location
   * @param longitude The longitude coordinate of the location
   * @return A Mono containing the WeatherData with current conditions, forecast and alerts
   * @throws WebClientResponseException if there is an error calling the weather API
   * @throws CircuitBreakerException if the circuit breaker is open due to too many failures
   */
  Mono<WeatherData> getWeatherData(Double latitude, Double longitude);

  /**
   * Converts a location name/address to geographic coordinates.
   *
   * @param location The location name or address to geocode
   * @return A Mono containing the Coordinates (latitude and longitude) for the specified location
   * @throws WebClientResponseException if there is an error calling the geocoding API
   * @throws CircuitBreakerException if the circuit breaker is open due to too many failures
   */
  Mono<Coordinates> getCoordinates(String location);
}
