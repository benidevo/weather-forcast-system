package com.weatherforecast.weatherservice.cache.impl;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.weatherforecast.weatherservice.domain.WeatherData;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class RedisWeatherCacheRepositoryTest {

  @Mock private ReactiveRedisTemplate<String, WeatherData> redisTemplate;

  @Mock private ReactiveValueOperations<String, WeatherData> valueOperations;

  private RedisWeatherCacheRepository repository;

  @BeforeEach
  void setUp() {
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    repository = new RedisWeatherCacheRepository(redisTemplate);
  }

  @Test
  void shouldSaveWeatherDataToRedis() {
    WeatherData weatherData = createSampleWeatherData();
    String expectedCacheKey = "weather:12.3400:45.6700";
    when(valueOperations.set(eq(expectedCacheKey), eq(weatherData), any(Duration.class)))
        .thenReturn(Mono.just(Boolean.TRUE));

    Mono<Void> result = repository.save(weatherData);

    StepVerifier.create(result).expectComplete().verify();

    verify(valueOperations).set(eq(expectedCacheKey), eq(weatherData), any(Duration.class));
  }

  @Test
  void shouldFindWeatherDataByCoordinates() {
    Double latitude = 12.34;
    Double longitude = 45.67;
    String expectedCacheKey = "weather:12.3400:45.6700";
    WeatherData expectedWeatherData = createSampleWeatherData();

    when(valueOperations.get(eq(expectedCacheKey))).thenReturn(Mono.just(expectedWeatherData));

    Mono<WeatherData> result = repository.findByCoordinates(latitude, longitude);

    StepVerifier.create(result).expectNext(expectedWeatherData).expectComplete().verify();

    verify(valueOperations).get(eq(expectedCacheKey));
  }

  @Test
  void shouldReturnEmptyWhenCacheMiss() {
    Double latitude = 12.34;
    Double longitude = 45.67;
    String expectedCacheKey = "weather:12.3400:45.6700";

    when(valueOperations.get(eq(expectedCacheKey))).thenReturn(Mono.empty());

    Mono<WeatherData> result = repository.findByCoordinates(latitude, longitude);

    StepVerifier.create(result).expectComplete().verify();

    verify(valueOperations).get(eq(expectedCacheKey));
  }

  @Test
  void shouldGenerateCacheKeyWithCorrectFormat() {
    Double latitude = 12.3456;
    Double longitude = 45.6789;
    String expectedCacheKey = "weather:12.3456:45.6789";

    when(valueOperations.get(eq(expectedCacheKey))).thenReturn(Mono.empty());

    repository.findByCoordinates(latitude, longitude);

    verify(valueOperations).get(eq(expectedCacheKey));
  }

  private WeatherData createSampleWeatherData() {
    var weatherData =
        WeatherData.builder()
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
