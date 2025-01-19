package com.project.shopapp.service;

import com.project.shopapp.entity.UserEntity;
import com.project.shopapp.model.dto.UserDto;
import com.project.shopapp.model.dto.UserLoginDto;
import com.project.shopapp.model.response.UserResponse;

public interface IUserService {
    UserResponse createUser(UserDto userDto);
    String login(UserLoginDto userLoginDto);
}
