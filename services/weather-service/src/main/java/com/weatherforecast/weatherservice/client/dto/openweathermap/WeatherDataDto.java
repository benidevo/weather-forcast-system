package com.weatherforecast.weatherservice.client.dto.openweathermap;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeatherDataDto {
    private Double lat;
    private Double lon;
    private String timezone;
    private Integer timezone_offset;
    private CurrentWeather current;
    private List<MinutelyWeather> minutely;
    private List<HourlyWeather> hourly;
    private List<DailyWeather> daily;
    private List<Alert> alerts;

    @Data
    public static class CurrentWeather {
        private Long dt;
        private Long sunrise;
        private Long sunset;
        private Double temp;
        private Double feels_like;
        private Integer pressure;
        private Integer humidity;
        private Double dew_point;
        private Double uvi;
        private Integer clouds;
        private Integer visibility;
        private Double wind_speed;
        private Integer wind_deg;
        private Double wind_gust;
        private List<Weather> weather;
    }

    @Data
    public static class MinutelyWeather {
        private Long dt;
        private Double precipitation;
    }

    @Data
    public static class HourlyWeather {
        private Long dt;
        private Double temp;
        private Double feels_like;
        private Integer pressure;
        private Integer humidity;
        private Double dew_point;
        private Double uvi;
        private Integer clouds;
        private Integer visibility;
        private Double wind_speed;
        private Integer wind_deg;
        private Double wind_gust;
        private List<Weather> weather;
        private Double pop;
    }

    @Data
    public static class DailyWeather {
        private Long dt;
        private Long sunrise;
        private Long sunset;
        private Long moonrise;
        private Long moonset;
        private Double moon_phase;
        private String summary;
        private Temperature temp;
        private FeelsLike feels_like;
        private Integer pressure;
        private Integer humidity;
        private Double dew_point;
        private Double wind_speed;
        private Integer wind_deg;
        private Double wind_gust;
        private List<Weather> weather;
        private Integer clouds;
        private Double pop;
        private Double rain;
        private Double uvi;
    }

    @Data
    public static class Temperature {
        private Double day;
        private Double min;
        private Double max;
        private Double night;
        private Double eve;
        private Double morn;
    }

    @Data
    public static class FeelsLike {
        private Double day;
        private Double night;
        private Double eve;
        private Double morn;
    }

    @Data
    public static class Weather {
        private Long id;
        private String main;
        private String description;
        private String icon;
    }

    @Data
    public static class Alert {
        private String sender_name;
        private String event;
        private Long start;
        private Long end;
        private String description;
        private List<String> tags;
    }
}
