package com.weatherforecast.weatherservice.cache;

import com.weatherforecast.weatherservice.domain.WeatherData;
import reactor.core.publisher.Mono;

public interface WeatherCacheRepository {
  Mono<Void> save(WeatherData weatherData);

  Mono<WeatherData> findByCoordinates(Double latitude, Double longitude);
}
