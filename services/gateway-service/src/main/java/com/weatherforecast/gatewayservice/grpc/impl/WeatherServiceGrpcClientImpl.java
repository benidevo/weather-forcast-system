package com.weatherforecast.gatewayservice.grpc.impl;

import com.weatherforecast.gatewayservice.dto.grpc.WeatherDataDto;
import com.weatherforecast.gatewayservice.dto.grpc.WeatherDataDto.Alert;
import com.weatherforecast.gatewayservice.dto.grpc.WeatherDataDto.Forecast;
import com.weatherforecast.gatewayservice.grpc.WeatherServiceGrpcClient;
import com.weatherforecast.weatherservice.grpc.CoordinatesRequest;
import com.weatherforecast.weatherservice.grpc.LocationRequest;
import com.weatherforecast.weatherservice.grpc.WeatherDataResponse;
import com.weatherforecast.weatherservice.grpc.WeatherServiceGrpc;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.PreDestroy;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class WeatherServiceGrpcClientImpl implements WeatherServiceGrpcClient {
  private final WeatherServiceGrpc.WeatherServiceStub asyncStub;
  private final ManagedChannel channel;
  private static final int channelTerminationTimeout = 5;
  private final CircuitBreaker circuitBreaker;

  public WeatherServiceGrpcClientImpl(
      @Value("${grpc.client.weather-service.address}") String address,
      CircuitBreaker circuitBreaker) {
    this.channel = ManagedChannelBuilder.forTarget(address).usePlaintext().build();
    this.asyncStub = WeatherServiceGrpc.newStub(channel);
    this.circuitBreaker = circuitBreaker;
  }

  @PreDestroy
  public void shutdown() {
    log.info("Shutting down weather service gRPC channel");
    if (channel != null && !channel.isShutdown()) {
      try {
        log.info("Waiting for {} seconds for gRPC channel to shut down", channelTerminationTimeout);
        channel.shutdown().awaitTermination(channelTerminationTimeout, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        log.error("Error shutting down gRPC channel", e);
        Thread.currentThread().interrupt();
      } finally {
        if (!channel.isTerminated()) {
          log.warn("gRPC channel did not shut down in time, forcing shutdown");
          channel.shutdownNow();
        }
      }
    }
    log.info("gRPC channel shut down successfully");
  }

  @Override
  public Mono<WeatherDataDto> getWeatherData(String location) {
    return Mono.<WeatherDataDto>create(
            sink -> {
              LocationRequest request = LocationRequest.newBuilder().setLocation(location).build();

              asyncStub
                  .withDeadlineAfter(channelTerminationTimeout, TimeUnit.SECONDS)
                  .getWeatherDataByLocation(
                      request,
                      new StreamObserver<WeatherDataResponse>() {
                        @Override
                        public void onNext(WeatherDataResponse response) {
                          log.info("Received weather data for location: {}", location);
                          sink.success(mapGrpcResponseToDto(response));
                        }

                        @Override
                        public void onError(Throwable t) {
                          log.error(
                              "Error occurred while fetching weather data for location: {}",
                              location,
                              t);
                          sink.error(t);
                        }

                        @Override
                        public void onCompleted() {
                          log.info("Completed fetching weather data for location: {}", location);
                        }
                      });
            })
        .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
        .onErrorResume(
            throwable -> {
              log.error(
                  "Circuit breaker triggered while fetching weather data for location: {}",
                  location,
                  throwable);
              return Mono.just(getFallbackWeatherData(location));
            });
  }

  @Override
  public Mono<WeatherDataDto> getWeatherDataByCoordinates(double lat, double lon) {
    return Mono.<WeatherDataDto>create(
            sink -> {
              CoordinatesRequest request =
                  CoordinatesRequest.newBuilder().setLatitude(lat).setLongitude(lon).build();

              asyncStub
                  .withDeadlineAfter(channelTerminationTimeout, TimeUnit.SECONDS)
                  .getWeatherData(
                      request,
                      new StreamObserver<WeatherDataResponse>() {
                        @Override
                        public void onNext(WeatherDataResponse response) {
                          log.info(
                              "Received weather data for coordinates: lat: {}, lon{}", lat, lon);
                          sink.success(mapGrpcResponseToDto(response));
                        }

                        @Override
                        public void onError(Throwable t) {
                          log.error(
                              "Error occurred while fetching weather data for coordinates: lat: {}, lon: {}",
                              lat,
                              lon,
                              t);
                          sink.error(t);
                        }

                        @Override
                        public void onCompleted() {
                          log.info(
                              "Completed fetching weather data for coordinates: lat: {}, lon: {}",
                              lat,
                              lon);
                        }
                      });
            })
        .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
        .onErrorResume(
            throwable -> {
              log.error(
                  "Circuit breaker triggered while fetching weather data for coordinates: lat: {}, lon: {}",
                  lat,
                  lon,
                  throwable);
              return Mono.just(getFallbackWeatherDataByCoordinates(lat, lon));
            });
  }

  private WeatherDataDto mapGrpcResponseToDto(WeatherDataResponse grpcResponse) {
    List<Alert> alerts =
        grpcResponse.getAlertsList().stream()
            .map(
                alertData ->
                    Alert.builder()
                        .name(alertData.getName())
                        .description(alertData.getDescription())
                        .startTime(alertData.getStartTime())
                        .endTime(alertData.getEndTime())
                        .build())
            .collect(Collectors.toList());

    List<Forecast> forecasts =
        grpcResponse.getForecastList().stream()
            .map(
                forecastData ->
                    Forecast.builder()
                        .description(forecastData.getDescription())
                        .temperature(forecastData.getTemperature())
                        .feelsLike(forecastData.getFeelsLike())
                        .pressure(forecastData.getPressure())
                        .humidity(forecastData.getHumidity())
                        .windSpeed(forecastData.getWindSpeed())
                        .build())
            .collect(Collectors.toList());

    return WeatherDataDto.builder()
        .latitude(grpcResponse.getLatitude())
        .longitude(grpcResponse.getLongitude())
        .timezone(grpcResponse.getTimezone())
        .timezoneOffset(grpcResponse.getTimezoneOffset())
        .temperature(grpcResponse.getTemperature())
        .humidity(grpcResponse.getHumidity())
        .description(grpcResponse.getDescription())
        .alerts(alerts)
        .forecast(forecasts)
        .build();
  }

  private WeatherDataDto getFallbackWeatherData(String location) {
    log.warn("Fallback weather data for location: {}", location);
    return WeatherDataDto.builder()
        .latitude(0.0)
        .longitude(0.0)
        .timezone("UTC")
        .timezoneOffset("0")
        .temperature(0.0)
        .humidity(0)
        .description("No data available")
        .alerts(Collections.emptyList())
        .forecast(Collections.emptyList())
        .build();
  }

  private WeatherDataDto getFallbackWeatherDataByCoordinates(double lat, double lon) {
    log.warn("Fallback weather data for coordinates: lat: {}, lon: {}", lat, lon);
    return WeatherDataDto.builder()
        .latitude(lat)
        .longitude(lon)
        .timezone("UTC")
        .timezoneOffset("0")
        .temperature(0.0)
        .humidity(0)
        .description("No data available")
        .alerts(Collections.emptyList())
        .forecast(Collections.emptyList())
        .build();
  }
}
