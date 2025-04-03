package com.weatherforecast.weatherservice.cache.impl;

// import com.weatherforecast.weatherservice.cache.WeatherCacheRepository;
import com.weatherforecast.weatherservice.config.TestConfig;
import com.weatherforecast.weatherservice.domain.WeatherData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Import(TestConfig.class)
public class RedisWeatherCacheRepositoryTest {

    @Mock
    private ReactiveRedisTemplate<String, WeatherData> redisTemplate;

    @Mock
    private ReactiveValueOperations<String, WeatherData> valueOperations;

    private RedisWeatherCacheRepository repository;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        repository = new RedisWeatherCacheRepository(redisTemplate);
    }

    @Test
    void shouldSaveWeatherDataToRedis() {
        // Given
        WeatherData weatherData = createSampleWeatherData();
        String expectedCacheKey = "weather:12.3400:45.6700";
        when(valueOperations.set(eq(expectedCacheKey), eq(weatherData), any(Duration.class)))
                .thenReturn(Mono.just(Boolean.TRUE));

        // When
        Mono<Void> result = repository.save(weatherData);

        // Then
        StepVerifier.create(result)
                .expectComplete()
                .verify();

        verify(valueOperations).set(eq(expectedCacheKey), eq(weatherData), any(Duration.class));
    }

    @Test
    void shouldFindWeatherDataByCoordinates() {
        // Given
        Double latitude = 12.34;
        Double longitude = 45.67;
        String expectedCacheKey = "weather:12.3400:45.6700";
        WeatherData expectedWeatherData = createSampleWeatherData();

        when(valueOperations.get(eq(expectedCacheKey)))
                .thenReturn(Mono.just(expectedWeatherData));

        // When
        Mono<WeatherData> result = repository.findByCoordinates(latitude, longitude);

        // Then
        StepVerifier.create(result)
                .expectNext(expectedWeatherData)
                .expectComplete()
                .verify();

        verify(valueOperations).get(eq(expectedCacheKey));
    }

    @Test
    void shouldReturnEmptyWhenCacheMiss() {
        // Given
        Double latitude = 12.34;
        Double longitude = 45.67;
        String expectedCacheKey = "weather:12.3400:45.6700";

        when(valueOperations.get(eq(expectedCacheKey)))
                .thenReturn(Mono.empty());

        // When
        Mono<WeatherData> result = repository.findByCoordinates(latitude, longitude);

        // Then
        StepVerifier.create(result)
                .expectComplete()
                .verify();

        verify(valueOperations).get(eq(expectedCacheKey));
    }

    @Test
    void shouldGenerateCacheKeyWithCorrectFormat() {
        // Given
        Double latitude = 12.3456;
        Double longitude = 45.6789;
        String expectedCacheKey = "weather:12.3456:45.6789";

        when(valueOperations.get(eq(expectedCacheKey)))
                .thenReturn(Mono.empty());

        // When
        repository.findByCoordinates(latitude, longitude);

        // Then
        verify(valueOperations).get(eq(expectedCacheKey));
    }

    private WeatherData createSampleWeatherData() {
        var weatherData = WeatherData.builder()
                .latitude(12.34)
                .longitude(45.67)
                .timezone("America/Los_Angeles")
                .timezoneOffset("-08:00")
                .description("Clear sky")
                .temperature(20.0)
                .feelsLike(19.0)
                .pressure(1013.0)
                .humidity(50)
                .windSpeed(5.0)
                .build();

        return weatherData;
    }

}