package com.weatherforecast.authservice.service.impl;

import com.weatherforecast.authservice.entity.User;
import com.weatherforecast.authservice.model.AuthToken;
import com.weatherforecast.authservice.service.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtServiceImpl implements JwtService {
  @Value("${app.jwt.secret-key}")
  private String secretKey;

  @Value("${app.jwt.token-validity-in-seconds}")
  private int tokenValidityInSeconds;

  @Override
  public AuthToken generateToken(User user) {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime expiryDate = now.plusSeconds(tokenValidityInSeconds);

    String token = createToken(new HashMap<>(), user.getUsername());

    return AuthToken.builder().success(Boolean.TRUE).token(token).expiresAt(expiryDate).build();
  }

  private String createToken(Map<String, Object> claims, String subject) {
    return Jwts.builder()
        .subject(subject)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + 1000 * tokenValidityInSeconds))
        .signWith(getSigningKey())
        .compact();
  }

  private SecretKey getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
