package com.weatherforecast.weatherservice.service.impl;

import org.springframework.stereotype.Service;

import com.weatherforecast.weatherservice.cache.WeatherCacheRepository;
import com.weatherforecast.weatherservice.client.WeatherApiClient;
import com.weatherforecast.weatherservice.domain.WeatherData;
import com.weatherforecast.weatherservice.service.WeatherService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class WeatherServiceImpl implements WeatherService {
    private final WeatherCacheRepository cacheRepository;
    private final WeatherApiClient weatherApiClient;

    public WeatherServiceImpl(WeatherCacheRepository cacheRepository, WeatherApiClient weatherApiClient) {
        this.cacheRepository = cacheRepository;
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
                        System.out.println("Weather data retrieved: " + weatherData);
                    } else {
                        System.out.println("No weather data found.");
                    }
                })
                .doOnError(error -> log.error("Error retrieving weather data: {}", error.getMessage()))
                .doOnSubscribe(subscription -> log
                        .info("Fetching weather data for coordinates: {} {}", latitude, longitude))
                .doOnTerminate(() -> log
                        .info("Weather data fetch completed for coordinates: {} {}", latitude, longitude));
    }
}
