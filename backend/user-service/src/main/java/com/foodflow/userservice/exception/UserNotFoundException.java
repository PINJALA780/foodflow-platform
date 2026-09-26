package com.foodflow.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(UUID id) {
        super(String.format("User with id '%s' not found", id));
    }

    public UserNotFoundException(String email) {
        super(String.format("User with email '%s' not found", email));
    }
}
