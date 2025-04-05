package com.weatherforecast.weatherservice.service;

import com.weatherforecast.weatherservice.domain.Coordinates;
import reactor.core.publisher.Mono;

/**
 * Service interface for handling location-based operations. Provides functionality to convert
 * string-based location identifiers into geographic coordinates.
 */
public interface LocationService {

  /**
   * Retrieves the geographic coordinates for the specified location. This method performs
   * asynchronous location lookup and returns a reactive result.
   *
   * @param location A string representing a location (e.g., city name, address, postal code)
   * @return A {@link Mono} containing the {@link Coordinates} for the specified location or an
   *     empty Mono if the location couldn't be found
   */
  Mono<Coordinates> getCoordinates(String location);
}
