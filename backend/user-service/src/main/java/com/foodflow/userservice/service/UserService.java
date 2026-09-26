package com.foodflow.userservice.service;

import com.foodflow.userservice.dto.UpdateUserRequest;
import com.foodflow.userservice.dto.UserResponse;

import java.util.UUID;

public interface UserService {

    UserResponse getUserById(UUID id);

    UserResponse getUserByEmail(String email);

    UserResponse updateUser(UUID id, UpdateUserRequest request);

    void deleteUser(UUID id);
}
