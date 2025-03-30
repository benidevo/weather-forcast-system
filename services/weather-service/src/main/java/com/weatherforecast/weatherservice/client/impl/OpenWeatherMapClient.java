package com.weatherforecast.weatherservice.client.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.stream.Collectors;

import com.weatherforecast.weatherservice.client.WeatherApiClient;
import com.weatherforecast.weatherservice.client.dto.openweathermap.WeatherDataDto;
import com.weatherforecast.weatherservice.domain.WeatherData;
import com.weatherforecast.weatherservice.domain.Forecast;
import com.weatherforecast.weatherservice.domain.Alert;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class OpenWeatherMapClient implements WeatherApiClient {

    private final WebClient webClient;
    private final String apiKey;
    private final CircuitBreaker circuitBreaker;

    public OpenWeatherMapClient(
            WebClient openWeatherMapWebClient,
            @Value("${openweathermap.api.key}") String apiKey,
            CircuitBreakerRegistry circuitBreakerRegistry) {
        this.webClient = openWeatherMapWebClient;
        this.apiKey = apiKey;
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("openWeatherMap");
    }

    @Override
    public Mono<WeatherData> getWeatherData(String city) {
        log.info("Fetching current weather for city: {}", city);

        var weatherDataDto = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/weather")
                        .queryParam("q", city)
                        .queryParam("appid", apiKey)
                        .queryParam("units", "metric")
                        .build())
                .retrieve()
                .bodyToMono(WeatherDataDto.class)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .doOnSuccess(result -> log.info("Successfully fetched current weather for: {}", city))
                .doOnError(error -> log.error("Error fetching current weather for {}: {}", city, error.getMessage()));

        return mapWeatherData(weatherDataDto);
    }

    private Mono<WeatherData> mapWeatherData(Mono<WeatherDataDto> weatherDataDto) {
        return weatherDataDto.map(dto -> WeatherData.builder()
                .timezone(dto.getTimezone())
                .timezoneOffset(String.valueOf(dto.getTimezone_offset()))
                .description(dto.getCurrent().getWeather().get(0).getDescription())
                .temperature(dto.getCurrent().getTemp())
                .feelsLike(dto.getCurrent().getFeels_like())
                .pressure(dto.getCurrent().getPressure().doubleValue())
                .humidity(dto.getCurrent().getHumidity())
                .windSpeed(dto.getCurrent().getWind_speed())
                .forecast(dto.getDaily().stream()
                        .map(daily -> Forecast.builder()
                                .description(daily.getWeather().get(0).getDescription())
                                .temperature(daily.getTemp().getDay())
                                .feelsLike(daily.getFeels_like().getDay())
                                .pressure(daily.getPressure().doubleValue())
                                .humidity(daily.getHumidity())
                                .windSpeed(daily.getWind_speed())
                                .build())
                        .collect(Collectors.toList()))
                .alerts(dto.getAlerts() != null ? dto.getAlerts().stream()
                        .map(alert -> Alert.builder()
                                .name(alert.getSender_name())
                                .description(alert.getDescription())
                                .startTime(String.valueOf(alert.getStart()))
                                .endTime(String.valueOf(alert.getEnd()))
                                .build())
                        .collect(Collectors.toList()) : null)
                .build());
    }
}
