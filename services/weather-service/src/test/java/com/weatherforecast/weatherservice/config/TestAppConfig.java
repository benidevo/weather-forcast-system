package com.weatherforecast.weatherservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherforecast.weatherservice.domain.WeatherData;
import com.weatherforecast.weatherservice.grpc.GrpcWeatherServiceImpl;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

@TestConfiguration
public class TestAppConfig {
  @Bean
  @Primary
  public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
    return Mockito.mock(ReactiveRedisConnectionFactory.class);
  }

  @Bean
  @Primary
  public GrpcWeatherServiceImpl grpcWeatherService() {
    return Mockito.mock(GrpcWeatherServiceImpl.class);
  }

  @Bean
  @Primary
  @SuppressWarnings("unchecked")
  public ReactiveRedisTemplate<String, WeatherData> weatherDataRedisTemplate(
      ReactiveRedisConnectionFactory factory, ObjectMapper objectMapper) {
    return Mockito.mock(
        ReactiveRedisTemplate.class,
        Mockito.withSettings().defaultAnswer(Mockito.RETURNS_DEEP_STUBS));
  }
}
