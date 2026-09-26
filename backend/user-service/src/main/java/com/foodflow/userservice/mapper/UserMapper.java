package com.foodflow.userservice.mapper;

import com.foodflow.userservice.dto.UserResponse;
import com.foodflow.userservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "emailVerified", target = "emailVerified")
    UserResponse toUserResponse(User user);
}
