package com.foodflow.userservice.service;

import com.foodflow.userservice.dto.AuthResponse;
import com.foodflow.userservice.dto.LoginRequest;
import com.foodflow.userservice.dto.RefreshTokenRequest;
import com.foodflow.userservice.dto.RegisterRequest;
import com.foodflow.userservice.dto.UserResponse;
import com.foodflow.userservice.entity.RefreshToken;
import com.foodflow.userservice.entity.Role;
import com.foodflow.userservice.entity.User;
import com.foodflow.userservice.entity.UserStatus;
import com.foodflow.userservice.exception.InvalidTokenException;
import com.foodflow.userservice.exception.UserAlreadyExistsException;
import com.foodflow.userservice.mapper.UserMapper;
import com.foodflow.userservice.repository.RefreshTokenRepository;
import com.foodflow.userservice.repository.UserRepository;
import com.foodflow.userservice.security.JwtTokenProvider;
import com.foodflow.userservice.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserMapper userMapper;

    @Mock
    private Authentication authentication;

    private AuthServiceImpl authService;

    private User sampleUser;
    private UserResponse sampleUserResponse;

    private final String accessToken = "access-token";
    private final String refreshTokenValue = "refresh-token";

    @BeforeEach
    void setUp() {

        authService = new AuthServiceImpl(
                userRepository,
                refreshTokenRepository,
                passwordEncoder,
                jwtTokenProvider,
                authenticationManager,
                userMapper,
                604800000L
        );

        sampleUser = User.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .passwordHash("encoded-password")
                .phone("+919876543210")
                .role(Role.CUSTOMER)
                .status(UserStatus.ACTIVE)
                .build();

        sampleUserResponse = UserResponse.builder()
                .id(sampleUser.getId())
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("+919876543210")
                .role(Role.CUSTOMER)
                .status(UserStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("Should register a new user successfully")
    void register_Success() {

        RegisterRequest request = RegisterRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("Password@123")
                .phone("+919876543210")
                .role(Role.CUSTOMER)
                .build();

        when(userRepository.existsByEmail(request.email()))
                .thenReturn(false);

        when(passwordEncoder.encode(request.password()))
                .thenReturn("encoded-password");

        when(userRepository.save(any(User.class)))
                .thenReturn(sampleUser);

        when(jwtTokenProvider.generateAccessToken(sampleUser))
                .thenReturn(accessToken);

        when(jwtTokenProvider.getExpirationMs())
                .thenReturn(900000L);

        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(userMapper.toUserResponse(sampleUser))
                .thenReturn(sampleUserResponse);

        AuthResponse result = authService.register(request);

        assertThat(result).isNotNull();
        assertThat(result.accessToken()).isEqualTo(accessToken);
        assertThat(result.tokenType()).isEqualTo(AuthResponse.BEARER);
        assertThat(result.expiresIn()).isEqualTo(900000L);
        assertThat(result.user()).isEqualTo(sampleUserResponse);
        assertThat(result.refreshToken()).isNotBlank();

        verify(userRepository).existsByEmail(request.email());
        verify(passwordEncoder).encode(request.password());
        verify(userRepository).save(any(User.class));
        verify(refreshTokenRepository).save(any(RefreshToken.class));
        verify(jwtTokenProvider).generateAccessToken(sampleUser);
        verify(userMapper).toUserResponse(sampleUser);
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when registering duplicate email")
    void register_DuplicateEmail() {

        RegisterRequest request = RegisterRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("Password@123")
                .phone("+919876543210")
                .role(Role.CUSTOMER)
                .build();

        when(userRepository.existsByEmail(request.email()))
                .thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("email")
                .hasMessageContaining(request.email());

        verify(userRepository).existsByEmail(request.email());

        verifyNoInteractions(
                passwordEncoder,
                jwtTokenProvider,
                refreshTokenRepository,
                userMapper
        );

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should login successfully and update last login time")
    void login_Success() {

        LoginRequest request = LoginRequest.builder()
                .email("john.doe@example.com")
                .password("Password@123")
                .build();

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(sampleUser);

        when(jwtTokenProvider.generateAccessToken(sampleUser))
                .thenReturn(accessToken);

        when(jwtTokenProvider.getExpirationMs())
                .thenReturn(900000L);

        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(userMapper.toUserResponse(sampleUser))
                .thenReturn(sampleUserResponse);

        AuthResponse result = authService.login(request);

        assertThat(result).isNotNull();
        assertThat(result.accessToken()).isEqualTo(accessToken);
        assertThat(result.refreshToken()).isNotBlank();
        assertThat(result.tokenType()).isEqualTo(AuthResponse.BEARER);

        // AuthResponse.user() contains UserResponse, not User.
        assertThat(result.user()).isEqualTo(sampleUserResponse);

        verify(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        verify(userRepository)
                .updateLastLoginAt(eq(sampleUser.getId()), any(Instant.class));

        verify(jwtTokenProvider)
                .generateAccessToken(sampleUser);

        verify(refreshTokenRepository)
                .save(any(RefreshToken.class));

        verify(userMapper)
                .toUserResponse(sampleUser);
    }

    @Test
    @DisplayName("Should rotate refresh token successfully")
    void refreshToken_Success() {

        RefreshToken storedToken = RefreshToken.builder()
                .id(UUID.randomUUID())
                .token(refreshTokenValue)
                .user(sampleUser)
                .expiresAt(Instant.now().plusSeconds(3600))
                .revoked(false)
                .build();

        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken(refreshTokenValue)
                .build();

        when(refreshTokenRepository.findByToken(refreshTokenValue))
                .thenReturn(Optional.of(storedToken));

        when(jwtTokenProvider.generateAccessToken(sampleUser))
                .thenReturn(accessToken);

        when(jwtTokenProvider.getExpirationMs())
                .thenReturn(900000L);

        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(userMapper.toUserResponse(sampleUser))
                .thenReturn(sampleUserResponse);

        AuthResponse result = authService.refreshToken(request);

        assertThat(result).isNotNull();
        assertThat(result.accessToken()).isEqualTo(accessToken);
        assertThat(result.refreshToken()).isNotBlank();
        assertThat(result.tokenType()).isEqualTo(AuthResponse.BEARER);
        assertThat(result.user()).isEqualTo(sampleUserResponse);
        assertThat(storedToken.isRevoked()).isTrue();

        verify(refreshTokenRepository)
                .findByToken(refreshTokenValue);

        verify(refreshTokenRepository, times(2))
                .save(any(RefreshToken.class));

        verify(jwtTokenProvider)
                .generateAccessToken(sampleUser);

        verify(userMapper)
                .toUserResponse(sampleUser);
    }

    @Test
    @DisplayName("Should reject refresh token when token does not exist")
    void refreshToken_NotFound() {

        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken(refreshTokenValue)
                .build();

        when(refreshTokenRepository.findByToken(refreshTokenValue))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("Refresh token not found or already revoked");

        verify(refreshTokenRepository)
                .findByToken(refreshTokenValue);

        verifyNoInteractions(
                jwtTokenProvider,
                userMapper
        );

        verify(refreshTokenRepository, never())
                .save(any());
    }

    @Test
    @DisplayName("Should reject revoked refresh token")
    void refreshToken_Revoked() {

        RefreshToken storedToken = RefreshToken.builder()
                .id(UUID.randomUUID())
                .token(refreshTokenValue)
                .user(sampleUser)
                .expiresAt(Instant.now().plusSeconds(3600))
                .revoked(true)
                .build();

        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken(refreshTokenValue)
                .build();

        when(refreshTokenRepository.findByToken(refreshTokenValue))
                .thenReturn(Optional.of(storedToken));

        assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("Refresh token is expired or revoked");

        verify(refreshTokenRepository)
                .findByToken(refreshTokenValue);

        verify(refreshTokenRepository, never())
                .save(any());

        verifyNoInteractions(
                jwtTokenProvider,
                userMapper
        );
    }

    @Test
    @DisplayName("Should reject expired refresh token")
    void refreshToken_Expired() {

        RefreshToken storedToken = RefreshToken.builder()
                .id(UUID.randomUUID())
                .token(refreshTokenValue)
                .user(sampleUser)
                .expiresAt(Instant.now().minusSeconds(3600))
                .revoked(false)
                .build();

        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken(refreshTokenValue)
                .build();

        when(refreshTokenRepository.findByToken(refreshTokenValue))
                .thenReturn(Optional.of(storedToken));

        assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("Refresh token is expired or revoked");

        verify(refreshTokenRepository)
                .findByToken(refreshTokenValue);

        verify(refreshTokenRepository, never())
                .save(any());

        verifyNoInteractions(
                jwtTokenProvider,
                userMapper
        );
    }

    @Test
    @DisplayName("Should revoke refresh token during logout")
    void logout_Success() {

        RefreshToken storedToken = RefreshToken.builder()
                .id(UUID.randomUUID())
                .token(refreshTokenValue)
                .user(sampleUser)
                .expiresAt(Instant.now().plusSeconds(3600))
                .revoked(false)
                .build();

        when(refreshTokenRepository.findByToken(refreshTokenValue))
                .thenReturn(Optional.of(storedToken));

        authService.logout(refreshTokenValue);

        assertThat(storedToken.isRevoked())
                .isTrue();

        verify(refreshTokenRepository)
                .findByToken(refreshTokenValue);

        verify(refreshTokenRepository)
                .save(storedToken);
    }

    @Test
    @DisplayName("Should do nothing when logging out with unknown refresh token")
    void logout_UnknownToken() {

        when(refreshTokenRepository.findByToken(refreshTokenValue))
                .thenReturn(Optional.empty());

        authService.logout(refreshTokenValue);

        verify(refreshTokenRepository)
                .findByToken(refreshTokenValue);

        verify(refreshTokenRepository, never())
                .save(any());
    }
}
