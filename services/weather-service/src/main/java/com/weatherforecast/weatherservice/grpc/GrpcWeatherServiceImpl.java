package com.weatherforecast.weatherservice.grpc;

import org.lognet.springboot.grpc.GRpcService;

import com.weatherforecast.weatherservice.service.WeatherService;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@GRpcService
public class GrpcWeatherServiceImpl extends WeatherServiceGrpc.WeatherServiceImplBase {
    private final WeatherService weatherService;

    public GrpcWeatherServiceImpl(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @Override
    public void getWeatherData(CoordinatesRequest request, StreamObserver<WeatherDataResponse> responseObserver) {
        log.info("Received gRPC request for coordinates: {}, {}", request.getLatitude(), request.getLongitude());

        weatherService.getWeatherData(request.getLatitude(), request.getLongitude())
                .subscribe(
                        weatherData -> {
                            WeatherDataResponse response = Adapter.toGrpcResponse(weatherData);
                            responseObserver.onNext(response);
                            responseObserver.onCompleted();
                            log.info("Successfully responded to gRPC request for weather data");
                        },
                        error -> {
                            log.error("Error occurred while processing gRPC request: {}", error.getMessage());
                            responseObserver.onError(error);
                        });
    }

    @Override
    public void getWeatherDataByLocation(LocationRequest request,
            StreamObserver<WeatherDataResponse> responseObserver) {
        log.info("Received gRPC request for location: {}", request.getLocation());
        weatherService.getWeatherData(request.getLocation())
                .subscribe(
                        weatherData -> {
                            WeatherDataResponse response = Adapter.toGrpcResponse(weatherData);
                            responseObserver.onNext(response);
                            responseObserver.onCompleted();
                            log.info("Successfully responded to gRPC request for weather data");
                        },
                        error -> {
                            log.error("Error occurred while processing gRPC request: {}", error.getMessage());
                            responseObserver.onError(error);
                        });

    }
}
