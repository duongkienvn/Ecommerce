package com.project.shopapp.controller;

import com.project.shopapp.entity.UserEntity;
import com.project.shopapp.exception.AppException;
import com.project.shopapp.exception.ErrorCode;
import com.project.shopapp.model.dto.UserDto;
import com.project.shopapp.model.dto.UserLoginDto;
import com.project.shopapp.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDto userDto) {
        if (!userDto.getPassword().equals(userDto.getRetypePassword())) {
            throw new AppException(ErrorCode.UNMATCHED_PASSWORD);
        }

        return ResponseEntity.ok(userService.createUser(userDto));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody UserLoginDto userLoginDto) {
        String token = userService.login(userLoginDto);
        return ResponseEntity.ok(token);
    }
}
