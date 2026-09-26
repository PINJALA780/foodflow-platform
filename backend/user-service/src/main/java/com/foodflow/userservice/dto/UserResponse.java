package com.foodflow.userservice.dto;

import com.foodflow.userservice.entity.Role;
import com.foodflow.userservice.entity.UserStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record UserResponse(
    UUID id,
    String firstName,
    String lastName,
    String email,
    String phone,
    Role role,
    UserStatus status,
    boolean emailVerified,
    Instant createdAt,
    Instant updatedAt,
    Instant lastLoginAt
) {}
