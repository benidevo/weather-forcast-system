package com.weatherforecast.weatherservice.service;

import com.weatherforecast.weatherservice.domain.WeatherData;

import reactor.core.publisher.Mono;

public interface WeatherService {
    Mono<WeatherData> getWeatherData(Double latitude, Double longitude);
}
