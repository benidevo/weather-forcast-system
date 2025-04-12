package com.weatherforecast.gatewayservice.controllers;

import com.weatherforecast.gatewayservice.dto.http.WeatherResponseDto;
import com.weatherforecast.gatewayservice.grpc.WeatherServiceGrpcClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/weather")
public class WeatherController {
    private final WeatherServiceGrpcClient weatherServiceGrpcClient;

    public WeatherController(WeatherServiceGrpcClient service) {
        this.weatherServiceGrpcClient = service;
    }

  @GetMapping
  public Mono<ResponseEntity<WeatherResponseDto>> getMethodName(
      @RequestParam(required = false) Double lat,
      @RequestParam(required = false) Double lon,
      @RequestParam(required = false) String city) {

    ResponseEntity<WeatherResponseDto> response;
    if (lat == null && lon == null && city == null) {
      response =
          ResponseEntity.badRequest()
              .body(
                  WeatherResponseDto.builder()
                      .success(false)
                      .message("Please provide either lat and lon or city.")
                      .build());
      return Mono.just(response);
    }

    if ((lat != null && lon == null)
        || (lat == null && lon != null)
        || (city != null && (lat != null || lon != null))) {
      response =
          ResponseEntity.badRequest()
              .body(
                  WeatherResponseDto.builder()
                      .success(false)
                      .message("Please provide either lat and lon or city.")
                      .build());
      return Mono.just(response);
    }

    if (lat != null && lon != null) {
      return weatherServiceGrpcClient
          .getWeatherDataByCoordinates(lat, lon)
          .map(
              data ->
                  ResponseEntity.ok()
                      .body(
                          WeatherResponseDto.builder()
                              .success(true)
                              .message("Weather data fetched successfully.")
                              .data(data)
                              .build()));
    }

    return weatherServiceGrpcClient
        .getWeatherData(city)
        .map(
            data ->
                ResponseEntity.ok()
                    .body(
                        WeatherResponseDto.builder()
                            .success(true)
                            .message("Weather data fetched successfully.")
                            .data(data)
                            .build()));
  }
}
