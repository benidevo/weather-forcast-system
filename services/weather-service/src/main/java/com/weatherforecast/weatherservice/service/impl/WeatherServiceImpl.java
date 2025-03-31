package com.weatherforecast.weatherservice.service.impl;

import org.springframework.stereotype.Service;

import com.weatherforecast.weatherservice.client.WeatherApiClient;
import com.weatherforecast.weatherservice.domain.WeatherData;
import com.weatherforecast.weatherservice.service.WeatherService;

import reactor.core.publisher.Mono;

@Service
public class WeatherServiceImpl implements WeatherService {

    private final WeatherApiClient weatherApiClient;

    public WeatherServiceImpl(WeatherApiClient weatherApiClient) {
        this.weatherApiClient = weatherApiClient;
    }

    @Override
    public Mono<WeatherData> getWeatherData(Double latitude, Double longitude) {
        return weatherApiClient.getWeatherData(latitude, longitude);
    }
}
