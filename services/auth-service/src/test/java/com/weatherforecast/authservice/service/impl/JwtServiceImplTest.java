package com.weatherforecast.authservice.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.weatherforecast.authservice.entity.User;
import com.weatherforecast.authservice.model.AuthToken;
import com.weatherforecast.authservice.service.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.LocalDateTime;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class JwtServiceImplTest {
  private String secretKey =
      "ThisIsALongerSecretKeyThatIsAtLeast32BytesInLengthAndThereforeMeetsTheSpecificationRequirementsForHmacSha256";
  private JwtService jwtService;

  @BeforeEach
  public void setup() {
    jwtService = new JwtServiceImpl();
    ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
    ReflectionTestUtils.setField(jwtService, "tokenValidityInSeconds", 3600);
  }

  @Test
  public void testGenerateToken() {
    User user = createSampleUser();
    AuthToken token = jwtService.generateToken(user);

    assertNotNull(token);
    assertInstanceOf(AuthToken.class, token);
    assertTrue(token.isSuccess());
    assertNotNull(token.getToken());
    assertInstanceOf(String.class, token.getToken());
    assertNotNull(token.getExpiresAt());
    assertInstanceOf(LocalDateTime.class, token.getExpiresAt());
  }

  @Test
  public void testGenerateToken_ContainsCorrectClaims() {
    User user = createSampleUser();
    AuthToken token = jwtService.generateToken(user);

    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    SecretKey verificationKey = Keys.hmacShaKeyFor(keyBytes);

    String username =
        Jwts.parser()
            .verifyWith(verificationKey)
            .build()
            .parseSignedClaims(token.getToken())
            .getPayload()
            .getSubject();

    assertEquals("testuser", username);
  }

  private User createSampleUser() {
    return User.builder().username("testuser").password("testpassword").build();
  }
}
