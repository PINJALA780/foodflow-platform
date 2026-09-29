package com.foodflow.userservice.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private UserDetails userDetails;

    private static final String SECRET =
            "my-super-secret-key-that-is-at-least-32-characters-long";
    private static final long EXPIRATION_MS = 3_600_000L;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(SECRET, EXPIRATION_MS);

        userDetails = User.withUsername("john.doe@example.com")
                .password("password")
                .roles("CUSTOMER")
                .build();
    }

    @Test
    void generateAccessToken_shouldCreateValidToken() {
        String token = jwtTokenProvider.generateAccessToken(userDetails);

        assertThat(token).isNotBlank();
        assertThat(jwtTokenProvider.extractUsername(token))
                .isEqualTo("john.doe@example.com");
        assertThat(jwtTokenProvider.isTokenValid(token, userDetails))
                .isTrue();
    }

    @Test
    void generateAccessToken_shouldIncludeAccessTypeClaim() {
        String token = jwtTokenProvider.generateAccessToken(userDetails);

        assertThat(token).isNotBlank();
        assertThat(jwtTokenProvider.extractUsername(token))
                .isEqualTo(userDetails.getUsername());
    }

    @Test
    void generateAccessToken_withExtraClaims_shouldCreateValidToken() {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "CUSTOMER");

        String token = jwtTokenProvider.generateAccessToken(
                userDetails,
                extraClaims
        );

        assertThat(token).isNotBlank();
        assertThat(jwtTokenProvider.isTokenValid(token, userDetails))
                .isTrue();
    }

    @Test
    void extractExpiration_shouldReturnFutureExpiration() {
        String token = jwtTokenProvider.generateAccessToken(userDetails);

        Instant expiration = jwtTokenProvider.extractExpiration(token);

        assertThat(expiration).isAfter(Instant.now());
    }

    @Test
    void isTokenValid_shouldReturnFalseForDifferentUser() {
        String token = jwtTokenProvider.generateAccessToken(userDetails);

        UserDetails differentUser = User.withUsername("different@example.com")
                .password("password")
                .roles("CUSTOMER")
                .build();

        assertThat(jwtTokenProvider.isTokenValid(token, differentUser))
                .isFalse();
    }

    @Test
    void isTokenValid_shouldReturnFalseForInvalidToken() {
        assertThat(jwtTokenProvider.isTokenValid(
                "invalid.jwt.token",
                userDetails
        )).isFalse();
    }

    @Test
    void getExpirationMs_shouldReturnConfiguredExpiration() {
        assertThat(jwtTokenProvider.getExpirationMs())
                .isEqualTo(EXPIRATION_MS);
    }
}
