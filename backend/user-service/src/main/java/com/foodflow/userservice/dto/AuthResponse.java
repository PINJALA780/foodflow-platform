package com.foodflow.userservice.dto;

import lombok.Builder;

@Builder
public record AuthResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresIn,
    UserResponse user
) {
    public static final String BEARER = "Bearer";
}
