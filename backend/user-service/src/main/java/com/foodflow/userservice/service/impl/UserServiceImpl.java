package com.foodflow.userservice.service.impl;

import com.foodflow.userservice.dto.UpdateUserRequest;
import com.foodflow.userservice.dto.UserResponse;
import com.foodflow.userservice.entity.User;
import com.foodflow.userservice.exception.UserNotFoundException;
import com.foodflow.userservice.mapper.UserMapper;
import com.foodflow.userservice.repository.UserRepository;
import com.foodflow.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        return userMapper.toUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (StringUtils.hasText(request.firstName())) {
            user.setFirstName(request.firstName());
        }
        if (StringUtils.hasText(request.lastName())) {
            user.setLastName(request.lastName());
        }
        if (StringUtils.hasText(request.email())) {
            user.setEmail(request.email());
        }
        if (StringUtils.hasText(request.phone())) {
            user.setPhone(request.phone());
        }

        User updated = userRepository.save(user);
        log.info("User updated: {}", updated.getId());
        return userMapper.toUserResponse(updated);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
        log.info("User deleted: {}", id);
    }
}
