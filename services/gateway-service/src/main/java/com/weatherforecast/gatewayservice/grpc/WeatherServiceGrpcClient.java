package com.weatherforecast.gatewayservice.grpc;

import com.weatherforecast.gatewayservice.dto.grpc.WeatherDataDto;
import reactor.core.publisher.Mono;

public interface WeatherServiceGrpcClient {
  /**
   * Fetches weather data for a given location.
   *
   * @param location the location for which to fetch weather data
   * @return a Mono containing the weather data
   */
  Mono<WeatherDataDto> getWeatherData(String location);

  /**
   * Fetches weather data for a given set of coordinates.
   *
   * @param lat the latitude
   * @param lon the longitude
   * @return a Mono containing the weather data
   */
  Mono<WeatherDataDto> getWeatherDataByCoordinates(double lat, double lon);
}
