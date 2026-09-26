package com.foodflow.userservice.service;

import com.foodflow.userservice.dto.AuthResponse;
import com.foodflow.userservice.dto.LoginRequest;
import com.foodflow.userservice.dto.RefreshTokenRequest;
import com.foodflow.userservice.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);

    void logout(String refreshToken);
}
