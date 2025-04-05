package com.weatherforecast.weatherservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;

@SpringBootApplication(exclude = {RedisAutoConfiguration.class})
public class WeatherServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(WeatherServiceApplication.class, args);
  }
}
