package com.weatherforecast.weatherservice.client.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.stream.Collectors;

import com.weatherforecast.weatherservice.client.WeatherApiClient;
import com.weatherforecast.weatherservice.client.dto.openweathermap.LocationDto;
import com.weatherforecast.weatherservice.client.dto.openweathermap.WeatherDataDto;
import com.weatherforecast.weatherservice.domain.WeatherData;
import com.weatherforecast.weatherservice.domain.Forecast;
import com.weatherforecast.weatherservice.domain.Alert;
import com.weatherforecast.weatherservice.domain.Coordinates;

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
                        WebClient webClient,
                        @Value("${openweathermap.api.key}") String apiKey,
                        CircuitBreakerRegistry circuitBreakerRegistry) {
                this.webClient = webClient;
                this.apiKey = apiKey;
                this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("openWeatherMap");
        }

        @Override
        public Mono<WeatherData> getWeatherData(Double latitude, Double longitude) {
                log.info("Fetching current weather for latitude: {} and longitude: {}", latitude, longitude);

                var weatherDataDto = webClient.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/data/3.0/onecall")
                                                .queryParam("lat", latitude)
                                                .queryParam("lon", longitude)
                                                .queryParam("appid", apiKey)
                                                .queryParam("units", "metric")
                                                .build())
                                .retrieve()
                                .bodyToMono(WeatherDataDto.class)
                                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                                .doOnSuccess(result -> log.info("Successfully fetched current weather for: {}",
                                                latitude, longitude))
                                .doOnError(error -> log.error("Error fetching current weather for {}: {}", latitude,
                                                longitude, error.getMessage()));

                return mapWeatherData(weatherDataDto);
        }

        public Mono<Coordinates> getCoordinates(String location) {
                log.info("Retrieving coordinates for location: {}", location);
                var locationDto = webClient.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/geo/1.0/direct")
                                                .queryParam("q", location)
                                                .queryParam("limit", 1)
                                                .queryParam("appid", apiKey)
                                                .build())
                                .retrieve()
                                .bodyToFlux(LocationDto.class)
                                .next()
                                .doOnSuccess(result -> log.info("Successfully retrieved coordinates for: {}", location))
                                .doOnError(error -> log.error("Error retrieving coordinates for {}: {}", location,
                                                error.getMessage()));

                return mapCoordinates(locationDto);

        }

        /**
         * Maps the Open Weather Map API response data to the application's domain
         * model.
         *
         * <p>
         * This method transforms a {@link Mono<WeatherDataDto>} received from the
         * OpenWeatherMap API
         * into a {@link Mono<WeatherData>} object used within the application. The
         * transformation includes
         * mapping basic geographic data, current weather conditions, daily forecasts,
         * and any weather alerts
         * that might be present in the API response.
         * </p>
         *
         * <p>
         * If no alerts are present in the API response, the alerts field in the
         * resulting
         * {@link WeatherData} object will be set to null.
         * </p>
         *
         * @param weatherDataDto a Mono containing the DTO received from the
         *                       OpenWeatherMap API
         * @return a Mono containing the transformed domain model object with weather
         *         information
         */
        private Mono<WeatherData> mapWeatherData(Mono<WeatherDataDto> weatherDataDto) {
                return weatherDataDto.map(dto -> WeatherData.builder()
                                .latitude(dto.getLat())
                                .longitude(dto.getLon())
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

        /**
         * Maps a LocationDto wrapped in a Mono to a Coordinates object.
         *
         * This method extracts latitude and longitude values from the provided
         * LocationDto
         * and creates a new Coordinates object with these values.
         *
         * @param locationDto the Mono containing the LocationDto to be mapped
         * @return a Mono containing a Coordinates object with the extracted latitude
         *         and longitude
         */
        private Mono<Coordinates> mapCoordinates(Mono<LocationDto> locationDto) {

                return locationDto.map(dto -> Coordinates.builder()
                                .latitude(dto.getLat())
                                .longitude(dto.getLon())
                                .build());
        }
}
