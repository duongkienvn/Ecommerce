package com.project.shopapp.controller;

import com.project.shopapp.exception.AppException;
import com.project.shopapp.exception.ErrorCode;
import com.project.shopapp.model.dto.ChangePassword;
import com.project.shopapp.model.dto.UserDto;
import com.project.shopapp.model.dto.UserLoginDto;
import com.project.shopapp.model.response.UserListResponse;
import com.project.shopapp.model.response.UserResponse;
import com.project.shopapp.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<UserResponse> getUserByPhoneNumber(@RequestParam("phone_number") String phoneNumber) {
        return ResponseEntity.ok(userService.getUserByPhoneNumber(phoneNumber));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserListResponse> getAllUsers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "5") int limit
    ) {
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("createdAt"));
        Page<UserResponse> userResponsePage = userService.findAllUsers(pageRequest);

        List<UserResponse> userResponseList = userResponsePage.getContent();
        int totalPages = userResponsePage.getTotalPages();

        return ResponseEntity.ok(UserListResponse.builder()
                .userResponseList(userResponseList)
                .totalPages(totalPages)
                .build());
    }

    @PostMapping("/{id}/change-password")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> changePassword(
            @PathVariable("id") Long userId,
            @RequestParam String currentPassword,
            @RequestBody ChangePassword changePassword) {
        userService.changePassword(userId, currentPassword, changePassword);
        return ResponseEntity.ok("Password is updated!");
    }
}
