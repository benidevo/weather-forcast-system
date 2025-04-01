package com.weatherforecast.weatherservice.cache.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherforecast.weatherservice.domain.WeatherData;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;

@Configuration
public class RedisConfig {
    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Bean
    @Primary
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisHost);
        config.setPort(redisPort);

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofSeconds(5))
                .shutdownTimeout(Duration.ofSeconds(2))
                .clientOptions(ClientOptions.builder()
                        .socketOptions(SocketOptions.builder()
                                .connectTimeout(Duration.ofSeconds(5))
                                .build())
                        .build())
                .build();

        return new LettuceConnectionFactory(config, clientConfig);
    }

    @Bean
    public ReactiveRedisTemplate<String, WeatherData> weatherDataRedisTemplate(
            ReactiveRedisConnectionFactory factory, ObjectMapper objectMapper) {
        Jackson2JsonRedisSerializer<WeatherData> serializer = new Jackson2JsonRedisSerializer<>(WeatherData.class);
        serializer.setObjectMapper(objectMapper);

        RedisSerializationContext<String, WeatherData> serializationContext = RedisSerializationContext
                .<String, WeatherData>newSerializationContext(new StringRedisSerializer())
                .value(serializer)
                .build();

        return new ReactiveRedisTemplate<>(factory, serializationContext);
    }
}
