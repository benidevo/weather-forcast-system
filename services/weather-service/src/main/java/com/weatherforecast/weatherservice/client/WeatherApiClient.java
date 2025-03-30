package com.weatherforecast.weatherservice.client;
import com.weatherforecast.weatherservice.domain.WeatherData;
import reactor.core.publisher.Mono;

public interface WeatherApiClient {

    /**
     * Retrieves current weather data for a specified city.
     *
     * @param city The name of the city to get weather data for
     * @return A Mono containing the WeatherData response, or an error if the request fails
     * @throws WebClientResponseException if there is an error with the HTTP request
     * @throws IllegalArgumentException if the city parameter is null or empty
     */
    Mono<WeatherData> getWeatherData(String city);
}
