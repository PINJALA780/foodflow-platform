package com.foodflow.userservice.security;

import com.foodflow.userservice.entity.Role;
import com.foodflow.userservice.entity.User;
import com.foodflow.userservice.entity.UserStatus;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;
    private User user;

    private final String token = "valid-jwt-token";

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(
                jwtTokenProvider,
                userDetailsService
        );

        user = User.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .passwordHash("encoded-password")
                .role(Role.CUSTOMER)
                .status(UserStatus.ACTIVE)
                .build();

        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldContinueFilterChainWhenAuthorizationHeaderIsMissing()
            throws ServletException, IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtTokenProvider, userDetailsService);

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();
    }

    @Test
    void shouldContinueFilterChainWhenAuthorizationHeaderIsNotBearer()
            throws ServletException, IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic abc123");

        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtTokenProvider, userDetailsService);

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();
    }

    @Test
    void shouldAuthenticateUserWhenValidBearerTokenIsProvided()
            throws ServletException, IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtTokenProvider.extractUsername(token))
                .thenReturn(user.getUsername());

        when(userDetailsService.loadUserByUsername(user.getUsername()))
                .thenReturn(user);

        when(jwtTokenProvider.isTokenValid(token, user))
                .thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isInstanceOf(UsernamePasswordAuthenticationToken.class);

        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .isEqualTo(user);

        verify(jwtTokenProvider).extractUsername(token);
        verify(userDetailsService).loadUserByUsername(user.getUsername());
        verify(jwtTokenProvider).isTokenValid(token, user);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateWhenTokenIsInvalid()
            throws ServletException, IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtTokenProvider.extractUsername(token))
                .thenReturn(user.getUsername());

        when(userDetailsService.loadUserByUsername(user.getUsername()))
                .thenReturn(user);

        when(jwtTokenProvider.isTokenValid(token, user))
                .thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();

        verify(jwtTokenProvider).extractUsername(token);
        verify(userDetailsService).loadUserByUsername(user.getUsername());
        verify(jwtTokenProvider).isTokenValid(token, user);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotReplaceExistingAuthentication()
            throws ServletException, IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);

        MockHttpServletResponse response = new MockHttpServletResponse();

        UsernamePasswordAuthenticationToken existingAuthentication =
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                );

        SecurityContextHolder.getContext()
                .setAuthentication(existingAuthentication);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isSameAs(existingAuthentication);

        verify(jwtTokenProvider).extractUsername(token);
        verifyNoInteractions(userDetailsService);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldContinueFilterChainWhenJwtProcessingThrowsException()
            throws ServletException, IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtTokenProvider.extractUsername(token))
                .thenThrow(new RuntimeException("Invalid JWT"));

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();

        verify(jwtTokenProvider).extractUsername(token);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldContinueFilterChainWhenUsernameIsNull()
            throws ServletException, IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtTokenProvider.extractUsername(token))
                .thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();

        verify(jwtTokenProvider).extractUsername(token);
        verifyNoInteractions(userDetailsService);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldContinueFilterChainWhenUserLookupFails()
            throws ServletException, IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtTokenProvider.extractUsername(token))
                .thenReturn(user.getUsername());

        when(userDetailsService.loadUserByUsername(user.getUsername()))
                .thenThrow(new RuntimeException("User not found"));

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();

        verify(jwtTokenProvider).extractUsername(token);
        verify(userDetailsService).loadUserByUsername(user.getUsername());
        verify(filterChain).doFilter(request, response);
    }
}
