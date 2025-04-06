package com.weatherforecast.gatewayservice.filter;

import com.weatherforecast.gatewayservice.grpc.AuthServiceGrpcClient;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JwtAuthenticationFilter implements WebFilter {
  private final AuthServiceGrpcClient grpcClient;
  private static final String BEARER_PREFIX = "Bearer ";
  private static final String AUTHORIZATION_HEADER = "Authorization";
  private final List<String> protectedPaths = List.of("/api/v1/weather");

  public JwtAuthenticationFilter(AuthServiceGrpcClient grpcClient) {
    this.grpcClient = grpcClient;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    String path = exchange.getRequest().getURI().getPath();

    if (!isProtectedPath(path)) {
      return chain.filter(exchange);
    }

    String token = extractTokenFromRequest(exchange.getRequest());

    if (token == null) {
      exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
      return exchange.getResponse().setComplete();
    }

    return grpcClient
        .isAuthenticated(token)
        .flatMap(
            validationResult -> {
              if (!validationResult) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
              }
              return chain.filter(exchange);
            })
        .onErrorResume(
            e -> {
              log.error("Error during token validation: {}", e.getMessage());
              exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
              return exchange.getResponse().setComplete();
            });
  }

  private boolean isProtectedPath(String path) {
    return protectedPaths.stream().anyMatch(path::startsWith);
  }

  private String extractTokenFromRequest(ServerHttpRequest request) {
    String token = request.getHeaders().getFirst(AUTHORIZATION_HEADER);
    if (token != null && token.startsWith(BEARER_PREFIX)) {
      return token.substring(BEARER_PREFIX.length());
    }
    return null;
  }
}
