package com.weatherforecast.weatherservice.service;

import com.weatherforecast.weatherservice.domain.WeatherData;

import reactor.core.publisher.Mono;


public interface WeatherService {

    /**
     * Retrieves weather data for a specific geographic location defined by coordinates.
     *
     * @param latitude The latitude coordinate of the location (decimal degrees)
     * @param longitude The longitude coordinate of the location (decimal degrees)
     * @return A Mono emitting the weather data for the specified coordinates when available
    */
    Mono<WeatherData> getWeatherData(Double latitude, Double longitude);

    /**
     * Retrieves weather data for a location specified by name or identifier.
     *
     * @param location A string representing the location name, city, address, or
     *                 identifier
     * @return A Mono emitting the weather data for the specified location when
     *         available
    */
    Mono<WeatherData> getWeatherData(String location);
}
