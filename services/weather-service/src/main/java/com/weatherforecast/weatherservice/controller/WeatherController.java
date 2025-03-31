package com.weatherforecast.weatherservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.weatherforecast.weatherservice.domain.WeatherData;
import com.weatherforecast.weatherservice.service.WeatherService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class WeatherController {
    public final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/weather")
    public Mono<ResponseEntity<WeatherData>> getWeather(
            @RequestParam(value = "lat") Double latitude,
            @RequestParam(value = "log") Double longitude) {

        if (latitude == null || longitude == null) {
            log.error("Latitude and Longitude must be provided");
            return Mono.just(ResponseEntity.badRequest().build());
        }

        return weatherService.getWeatherData(latitude, longitude)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorResume(e -> {
                    log.error("Error fetching weather data: {}", e.getMessage());
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }
}
