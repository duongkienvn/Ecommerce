package com.project.shopapp.service;

import com.project.shopapp.entity.UserEntity;
import com.project.shopapp.model.dto.UserDto;
import com.project.shopapp.model.dto.UserLoginDto;
import com.project.shopapp.model.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface IUserService {
    UserResponse createUser(UserDto userDto);
    String login(UserLoginDto userLoginDto);
    UserResponse getUserByPhoneNumber(String phonenumber);
    Page<UserResponse> findAllUsers(PageRequest pageRequest);
}
