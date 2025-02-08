package com.project.shopapp.controller;

import com.project.shopapp.exception.AppException;
import com.project.shopapp.exception.ErrorCode;
import com.project.shopapp.model.dto.ChangePassword;
import com.project.shopapp.model.dto.UserDto;
import com.project.shopapp.model.dto.UserLoginDto;
import com.project.shopapp.model.request.UserUpdateRequest;
import com.project.shopapp.model.response.ApiResponse;
import com.project.shopapp.model.response.PageResponse;
import com.project.shopapp.model.response.UserResponse;
import com.project.shopapp.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final IUserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDto userDto) {
        if (!userDto.getPassword().equals(userDto.getRetypePassword())) {
            throw new AppException(ErrorCode.UNMATCHED_PASSWORD);
        }
        log.info("User {} registered successfully.", userDto.getEmail());
        return ResponseEntity.ok(new ApiResponse(
                HttpStatus.CREATED.value(),
                "Register success!",
                userService.createUser(userDto)));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDto userLoginDto) {
        log.info("User {} attemping to login", userLoginDto.getPhoneNumber());
        String token = userService.login(userLoginDto);
        log.info("User {} loged in successfully!", userLoginDto.getPhoneNumber());
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Login success!", token));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getUserByPhoneNumber(@RequestParam("phone_number") String phoneNumber) {
        return ResponseEntity.ok(new ApiResponse(
                HttpStatus.OK.value(),
                "Get User successfully!",
                userService.getUserByPhoneNumber(phoneNumber)));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers(Pageable pageable) {
        Page<UserResponse> userResponsePage = userService.findAllUsers(pageable);
        List<UserResponse> userResponseList = userResponsePage.getContent();
        int totalPages = userResponsePage.getTotalPages();

        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(),
                "Get all users successfully!",
                PageResponse.builder()
                        .data(userResponseList)
                        .totalPages(totalPages)
                        .build()));
    }

    @PostMapping("/{id}/change-password")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> changePassword(
            @PathVariable("id") Long userId,
            @RequestParam String currentPassword,
            @RequestBody ChangePassword changePassword) {
        userService.changePassword(userId, currentPassword, changePassword);
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(),
                "Password is updated!"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserUpdateRequest userUpdateRequest,
                                        @PathVariable("id") Long userId) {
        return ResponseEntity.ok(new ApiResponse(
                HttpStatus.OK.value(),
                "Update success!",
                userService.updateUser(userId, userUpdateRequest)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Delete user successfully!"));
    }

    @GetMapping("/my-info")
    public ResponseEntity<?> getMyInfo() {
        return ResponseEntity.ok(new ApiResponse(
                HttpStatus.OK.value(),
                "Get success!",
                userService.getMyInfo()));
    }
}
