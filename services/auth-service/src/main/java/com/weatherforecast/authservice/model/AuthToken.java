package com.weatherforecast.authservice.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthToken {
    private boolean success;
    private String token;
    private String refreshToken;
    private LocalDateTime expiresAt;
}
