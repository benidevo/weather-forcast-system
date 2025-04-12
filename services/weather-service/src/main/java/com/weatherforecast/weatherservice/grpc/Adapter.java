package com.weatherforecast.weatherservice.grpc;

import com.weatherforecast.weatherservice.domain.Alert;
import com.weatherforecast.weatherservice.domain.Forecast;
import com.weatherforecast.weatherservice.domain.WeatherData;

/**
 * The Adapter class provides utility methods to convert between domain models and gRPC protocol
 * buffers. It contains methods to transform WeatherData domain objects to gRPC responses and gRPC
 * requests to coordinate data used by the application.
 *
 * <p>This class serves as a bridge between the domain layer and the gRPC communication layer,
 * ensuring proper data transformation while maintaining separation of concerns.
 */
public class Adapter {
  static WeatherDataResponse toGrpcResponse(WeatherData weatherData) {
    WeatherDataResponse.Builder responseBuilder =
        WeatherDataResponse.newBuilder()
            .setLatitude(weatherData.getLatitude())
            .setLongitude(weatherData.getLongitude())
            .setTimezone(weatherData.getTimezone())
            .setTimezoneOffset(weatherData.getTimezoneOffset())
            .setDescription(weatherData.getDescription())
            .setTemperature(weatherData.getTemperature())
            .setFeelsLike(weatherData.getFeelsLike())
            .setPressure(weatherData.getPressure())
            .setHumidity(weatherData.getHumidity())
            .setWindSpeed(weatherData.getWindSpeed());

    if (weatherData.getForecast() != null) {
      for (Forecast forecast : weatherData.getForecast()) {
        ForecastData forecastData =
            ForecastData.newBuilder()
                .setDescription(forecast.getDescription())
                .setTemperature(forecast.getTemperature())
                .setFeelsLike(forecast.getFeelsLike())
                .setPressure(forecast.getPressure())
                .setHumidity(forecast.getHumidity())
                .setWindSpeed(forecast.getWindSpeed())
                .build();
        responseBuilder.addForecast(forecastData);
      }
    }

    if (weatherData.getAlerts() != null) {
      for (Alert alert : weatherData.getAlerts()) {
        AlertData alertData =
            AlertData.newBuilder()
                .setName(alert.getName())
                .setDescription(alert.getDescription())
                .setStartTime(alert.getStartTime())
                .setEndTime(alert.getEndTime())
                .build();
        responseBuilder.addAlerts(alertData);
      }
    }

    return responseBuilder.build();
  }
}
