package com.weatherforecast.authservice.config;

import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
import org.lognet.springboot.grpc.GRpcServerBuilderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class ApplicationConfig {
  @Bean
  public GRpcServerBuilderConfigurer customGrpcServerBuilderConfigurer() {
    return new GRpcServerBuilderConfigurer() {
      @Override
      public void configure(io.grpc.ServerBuilder<?> serverBuilder) {
        if (serverBuilder instanceof NettyServerBuilder) {
          ((NettyServerBuilder) serverBuilder).addService(ProtoReflectionService.newInstance());
        }
      }
    };
  }
}
