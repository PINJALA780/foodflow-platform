package com.foodflow.userservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtTokenProvider(
            @Value("${application.jwt.secret}") String secret,
            @Value("${application.jwt.expiration-ms}") long expirationMs) {

        this.signingKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
        this.expirationMs = expirationMs;
    }

    // ---------------------------------------------------------------- //
    //                       Token Generation                            //
    // ---------------------------------------------------------------- //

    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "access");

        return buildToken(
                claims,
                userDetails.getUsername(),
                expirationMs
        );
    }

    public String generateAccessToken(
            UserDetails userDetails,
            Map<String, Object> extraClaims) {

        extraClaims.put("type", "access");

        return buildToken(
                extraClaims,
                userDetails.getUsername(),
                expirationMs
        );
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            String subject,
            long expirationMillis) {

        Instant issuedAt = Instant.now();
        Instant expiration = issuedAt.plusMillis(expirationMillis);

        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(toJwtDate(issuedAt))
                .expiration(toJwtDate(expiration))
                .id(UUID.randomUUID().toString())
                .signWith(signingKey)
                .compact();
    }

    /*
     * JJWT 0.12.x exposes issuedAt() and expiration() using
     * java.util.Date. The application uses java.time.Instant everywhere
     * else, so this method is the single compatibility boundary.
     */
    @SuppressWarnings("java:S2143")
    private Date toJwtDate(Instant instant) {
        return Date.from(instant);
    }

    // ---------------------------------------------------------------- //
    //                         Token Validation                          //
    // ---------------------------------------------------------------- //

    public boolean isTokenValid(
            String token,
            UserDetails userDetails) {

        try {
            final String username = extractUsername(token);

            return username.equals(userDetails.getUsername())
                    && !isTokenExpired(token);

        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).isBefore(Instant.now());
    }

    // ---------------------------------------------------------------- //
    //                         Claims Extraction                         //
    // ---------------------------------------------------------------- //

    public String extractUsername(String token) {
        return extractClaim(
                token,
                Claims::getSubject
        );
    }

    public Instant extractExpiration(String token) {
        return extractClaim(
                token,
                claims -> claims.getExpiration().toInstant()
        );
    }

    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver) {

        return claimsResolver.apply(
                extractAllClaims(token)
        );
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getExpirationMs() {
        return expirationMs;
    }
}
