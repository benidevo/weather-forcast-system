package com.weatherforecast.weatherservice.grpc;

import org.lognet.springboot.grpc.GRpcServerBuilderConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;

@Configuration
public class GrpcConfig {
    @Value("${grpc.server.port:9090}")
    private int grpcPort;

    @Bean
    public GRpcServerBuilderConfigurer grpcServerBuilderConfigurer() {
        return new GRpcServerBuilderConfigurer() {
            @Override
            public void configure(io.grpc.ServerBuilder<?> serverBuilder) {
                NettyServerBuilder.forPort(grpcPort);
            }
        };
    }

    @Bean
    public ProtoReflectionService reflectionService() {
        return (ProtoReflectionService) ProtoReflectionService.newInstance();
    }
}