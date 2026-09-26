package com.foodflow.userservice.service.impl;

import com.foodflow.userservice.dto.AuthResponse;
import com.foodflow.userservice.dto.LoginRequest;
import com.foodflow.userservice.dto.RefreshTokenRequest;
import com.foodflow.userservice.dto.RegisterRequest;
import com.foodflow.userservice.dto.UserResponse;
import com.foodflow.userservice.entity.RefreshToken;
import com.foodflow.userservice.entity.User;
import com.foodflow.userservice.entity.UserStatus;
import com.foodflow.userservice.exception.InvalidTokenException;
import com.foodflow.userservice.exception.UserAlreadyExistsException;
import com.foodflow.userservice.mapper.UserMapper;
import com.foodflow.userservice.repository.RefreshTokenRepository;
import com.foodflow.userservice.repository.UserRepository;
import com.foodflow.userservice.security.JwtTokenProvider;
import com.foodflow.userservice.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final long refreshExpirationMs;

    public AuthServiceImpl(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            AuthenticationManager authenticationManager,
            UserMapper userMapper,
            @Value("${application.jwt.refresh-expiration-ms}") long refreshExpirationMs) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.userMapper = userMapper;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.email());

        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("email", request.email());
        }

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .phone(request.phone())
                .role(request.role())
                .status(UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with id: {}", savedUser.getId());

        return buildAuthResponse(savedUser);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.email());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = (User) authentication.getPrincipal();
        userRepository.updateLastLoginAt(user.getId(), Instant.now());

        log.info("User logged in successfully: {}", user.getId());
        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new InvalidTokenException("Refresh token not found or already revoked"));

        if (storedToken.isRevoked() || storedToken.isExpired()) {
            throw new InvalidTokenException("Refresh token is expired or revoked");
        }

        User user = storedToken.getUser();

        // Rotate refresh token
        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        return buildAuthResponse(user);
    }

    @Override
    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
            log.info("Refresh token revoked for user: {}", token.getUser().getId());
        });
    }

    // ---------------------------------------------------------------- //
    //                         Private Helpers                           //
    // ---------------------------------------------------------------- //

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String newRefreshToken = createRefreshToken(user);
        UserResponse userResponse = userMapper.toUserResponse(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .tokenType(AuthResponse.BEARER)
                .expiresIn(jwtTokenProvider.getExpirationMs())
                .user(userResponse)
                .build();
    }

    private String createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiresAt(Instant.now().plusMillis(refreshExpirationMs))
                .build();
        return refreshTokenRepository.save(refreshToken).getToken();
    }
}
