package com.foodflow.userservice.service;

import com.foodflow.userservice.dto.UpdateUserRequest;
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
import static org.mockito.Mockito.verifyNoInteractions;
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
                .phone("9876543210")
                .role(Role.CUSTOMER)
                .status(UserStatus.ACTIVE)
                .build();

        sampleUserResponse = UserResponse.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("9876543210")
                .role(Role.CUSTOMER)
                .status(UserStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("Should return UserResponse when user exists by ID")
    void getUserById_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(sampleUser));
        when(userMapper.toUserResponse(sampleUser)).thenReturn(sampleUserResponse);

        UserResponse result = userService.getUserById(userId);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(userId);
        assertThat(result.email()).isEqualTo("john.doe@example.com");

        verify(userRepository).findById(userId);
        verify(userMapper).toUserResponse(sampleUser);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user does not exist by ID")
    void getUserById_NotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findById(userId);
        verifyNoInteractions(userMapper);
    }

    @Test
    @DisplayName("Should return UserResponse when user exists by email")
    void getUserByEmail_Success() {
        String email = "john.doe@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sampleUser));
        when(userMapper.toUserResponse(sampleUser)).thenReturn(sampleUserResponse);

        UserResponse result = userService.getUserByEmail(email);

        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo(email);

        verify(userRepository).findByEmail(email);
        verify(userMapper).toUserResponse(sampleUser);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when email does not exist")
    void getUserByEmail_NotFound() {
        String email = "missing@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByEmail(email))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findByEmail(email);
        verifyNoInteractions(userMapper);
    }

    @Test
    @DisplayName("Should update all supplied user fields")
    void updateUser_AllFields() {
        UpdateUserRequest request = new UpdateUserRequest(
                "Jane",
                "Smith",
                "jane.smith@example.com",
                "9123456789"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(sampleUser)).thenReturn(sampleUser);
        when(userMapper.toUserResponse(sampleUser)).thenReturn(sampleUserResponse);

        UserResponse result = userService.updateUser(userId, request);

        assertThat(sampleUser.getFirstName()).isEqualTo("Jane");
        assertThat(sampleUser.getLastName()).isEqualTo("Smith");
        assertThat(sampleUser.getEmail()).isEqualTo("jane.smith@example.com");
        assertThat(sampleUser.getPhone()).isEqualTo("9123456789");

        assertThat(result).isNotNull();

        verify(userRepository).findById(userId);
        verify(userRepository).save(sampleUser);
        verify(userMapper).toUserResponse(sampleUser);
    }

    @Test
    @DisplayName("Should keep existing values when update fields are blank")
    void updateUser_BlankFields() {
        String originalFirstName = sampleUser.getFirstName();
        String originalLastName = sampleUser.getLastName();
        String originalEmail = sampleUser.getEmail();
        String originalPhone = sampleUser.getPhone();

        UpdateUserRequest request = new UpdateUserRequest(
                " ",
                "",
                null,
                "   "
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(sampleUser)).thenReturn(sampleUser);
        when(userMapper.toUserResponse(sampleUser)).thenReturn(sampleUserResponse);

        userService.updateUser(userId, request);

        assertThat(sampleUser.getFirstName()).isEqualTo(originalFirstName);
        assertThat(sampleUser.getLastName()).isEqualTo(originalLastName);
        assertThat(sampleUser.getEmail()).isEqualTo(originalEmail);
        assertThat(sampleUser.getPhone()).isEqualTo(originalPhone);

        verify(userRepository).save(sampleUser);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when updating missing user")
    void updateUser_NotFound() {
        UpdateUserRequest request = new UpdateUserRequest(
                "Jane",
                "Smith",
                "jane@example.com",
                "9123456789"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(userId, request))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findById(userId);
        verifyNoInteractions(userMapper);
    }

    @Test
    @DisplayName("Should delete existing user")
    void deleteUser_Success() {
        when(userRepository.existsById(userId)).thenReturn(true);

        userService.deleteUser(userId);

        verify(userRepository).existsById(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when deleting missing user")
    void deleteUser_NotFound() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(userId))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).existsById(userId);
    }
}
