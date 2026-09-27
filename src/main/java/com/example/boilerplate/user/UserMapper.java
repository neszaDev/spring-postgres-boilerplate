package com.example.boilerplate.user;

import com.example.boilerplate.model.dto.UserResponse;
import com.example.boilerplate.model.User;nimport org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
  UserResponse toResponse(User user);
}


