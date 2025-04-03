package com.weatherforecast.weatherservice.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherforecast.weatherservice.domain.WeatherData;

@TestConfiguration
public class TestConfig {
    @Bean
    @Primary
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        return Mockito.mock(ReactiveRedisConnectionFactory.class);
    }

    @Bean
    @Primary
    public ReactiveRedisTemplate<String, WeatherData> weatherDataRedisTemplate(ReactiveRedisConnectionFactory factory,
            ObjectMapper objectMapper) {
        return Mockito.mock(ReactiveRedisTemplate.class);
    }
}