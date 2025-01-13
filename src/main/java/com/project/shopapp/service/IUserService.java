package com.project.shopapp.service;

import com.project.shopapp.entity.UserEntity;
import com.project.shopapp.model.dto.UserDto;
import com.project.shopapp.model.dto.UserLoginDto;

public interface IUserService {
    UserEntity createUser(UserDto userDto);
    String login(UserLoginDto userLoginDto);
}
