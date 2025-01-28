package com.project.shopapp.service;

import com.project.shopapp.entity.UserEntity;
import com.project.shopapp.model.dto.ChangePassword;
import com.project.shopapp.model.dto.UserDto;
import com.project.shopapp.model.dto.UserLoginDto;
import com.project.shopapp.model.request.UserUpdateRequest;
import com.project.shopapp.model.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface IUserService {
    UserEntity getById(Long id);
    UserResponse createUser(UserDto userDto);
    String login(UserLoginDto userLoginDto);
    UserResponse getUserByPhoneNumber(String phonenumber);
    void deleteUser(Long userId);
    UserResponse updateUser(Long id, UserUpdateRequest userUpdateRequest);
    Page<UserResponse> findAllUsers(PageRequest pageRequest);
    void existByEmail(String email);
    void updateByEmailAndPassword(String email, ChangePassword password);
    void changePassword(Long userId, String currentPassword, ChangePassword changePassword);
    UserResponse getMyInfo();
}
