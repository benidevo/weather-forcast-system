package com.weatherforecast.gatewayservice.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

  @Bean
  public CircuitBreaker weatherServiceCircuitBreaker() {
    CircuitBreakerConfig config =
        CircuitBreakerConfig.custom()
            .failureRateThreshold(50) // 50% failure rate
            .waitDurationInOpenState(Duration.ofSeconds(30)) // 30 seconds in open state
            .permittedNumberOfCallsInHalfOpenState(5) // 5 calls in half-open state
            .slidingWindowSize(10) // 10 requests
            .minimumNumberOfCalls(5) // Minimum 5 calls to consider circuit breaker
            .build();
    return CircuitBreaker.of("weatherServiceCircuitBreaker", config);
  }
}
