package com.weatherforecast.weatherservice.cache.impl;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;

import com.weatherforecast.weatherservice.cache.WeatherCacheRepository;
import com.weatherforecast.weatherservice.domain.WeatherData;

import reactor.core.publisher.Mono;
import java.time.Duration;

@Repository
public class RedisWeatherCacheRepository implements WeatherCacheRepository {
    private final static Duration CACHE_TTL = Duration.ofHours(1);
    private final ReactiveRedisTemplate<String, WeatherData> redisTemplate;

    public RedisWeatherCacheRepository(ReactiveRedisTemplate<String, WeatherData> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<Void> save(WeatherData weatherData) {
        String cacheKey = generateCacheKey(weatherData.getLatitude(), weatherData.getLongitude());
        return redisTemplate.opsForValue().set(cacheKey, weatherData, CACHE_TTL).then();
    }

    @Override
    public Mono<WeatherData> findByCoordinates(Double latitude, Double longitude) {
        String cacheKey = generateCacheKey(latitude, longitude);
        return redisTemplate.opsForValue().get(cacheKey);
    }

    private String generateCacheKey(Double latitude, Double longitude) {
        return String.format("weather:%.4f:%.4f", latitude, longitude);
    }

}
