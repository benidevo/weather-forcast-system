package com.weatherforecast.weatherservice.service.impl;

import org.springframework.stereotype.Service;

import com.weatherforecast.weatherservice.cache.WeatherCacheRepository;
import com.weatherforecast.weatherservice.client.WeatherApiClient;
import com.weatherforecast.weatherservice.domain.WeatherData;
import com.weatherforecast.weatherservice.service.LocationService;
import com.weatherforecast.weatherservice.service.WeatherService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class WeatherServiceImpl implements WeatherService {
    private final WeatherCacheRepository cacheRepository;
    private final LocationService locationService;
    private final WeatherApiClient weatherApiClient;

    public WeatherServiceImpl(WeatherCacheRepository cacheRepository, LocationService locationService,
            WeatherApiClient weatherApiClient) {
        this.cacheRepository = cacheRepository;
        this.locationService = locationService;
        this.weatherApiClient = weatherApiClient;
    }

    @Override
    public Mono<WeatherData> getWeatherData(Double latitude, Double longitude) {
        return cacheRepository.findByCoordinates(latitude, longitude)
                .switchIfEmpty(
                        weatherApiClient.getWeatherData(latitude, longitude)
                                .flatMap(weatherData -> cacheRepository.save(weatherData)
                                        .thenReturn(weatherData)))
                .doOnSuccess(weatherData -> {
                    if (weatherData != null) {
                        log.info("Weather data retrieved: {}", weatherData);
                    } else {
                        log.info("No weather data found.");
                    }
                })
                .doOnError(error -> log.error("Error retrieving weather data: {}", error.getMessage()))
                .doOnSubscribe(subscription -> log
                        .info("Fetching weather data for coordinates: {} {}", latitude, longitude))
                .doOnTerminate(() -> log
                        .info("Weather data fetch completed for coordinates: {} {}", latitude, longitude));
    }

    @Override
    public Mono<WeatherData> getWeatherData(String location) {
        return locationService.getCoordinates(location)
                .flatMap(coordinates -> getWeatherData(coordinates.getLatitude(), coordinates.getLongitude()))
                .doOnSuccess(weather -> {
                    if (weather != null) {
                        log.info(location + " weather data retrieved: " + weather);
                    } else {
                        log.info("No weather data found for location: " + location);
                    }
                })
                .doOnError(error -> log.error("Error retrieving weather data for location: {} Error: {}", location,
                        error.getMessage()))
                .doOnSubscribe(subscription -> log.info("Fetching weather data for location: {}", location))
                .doOnTerminate(() -> log.info("Weather data fetch completed for location: {}", location));
    }
}
