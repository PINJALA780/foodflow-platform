package com.foodflow.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateUserRequest(

    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    String firstName,

    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    String lastName,

    @Email(message = "Email must be a valid email address")
    String email,

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number must be a valid E.164 format")
    String phone

) {}
