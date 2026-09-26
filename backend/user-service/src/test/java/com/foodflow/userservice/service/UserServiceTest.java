package com.foodflow.userservice.service;

import com.foodflow.userservice.dto.UserResponse;
import com.foodflow.userservice.entity.Role;
import com.foodflow.userservice.entity.User;
import com.foodflow.userservice.entity.UserStatus;
import com.foodflow.userservice.exception.UserNotFoundException;
import com.foodflow.userservice.mapper.UserMapper;
import com.foodflow.userservice.repository.UserRepository;
import com.foodflow.userservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User sampleUser;
    private UserResponse sampleUserResponse;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        sampleUser = User.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .role(Role.CUSTOMER)
                .status(UserStatus.ACTIVE)
                .build();

        sampleUserResponse = UserResponse.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .role(Role.CUSTOMER)
                .status(UserStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("Should return UserResponse when user exists")
    void getUserById_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(sampleUser));
        when(userMapper.toUserResponse(sampleUser)).thenReturn(sampleUserResponse);

        UserResponse result = userService.getUserById(userId);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(userId);
        assertThat(result.email()).isEqualTo("john.doe@example.com");
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user does not exist")
    void getUserById_NotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(UserNotFoundException.class);
        verify(userRepository).findById(userId);
    }
}
