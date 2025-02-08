package com.project.shopapp.controller;

import com.nimbusds.jose.JOSEException;
import com.project.shopapp.model.dto.ChangePassword;
import com.project.shopapp.model.request.LogoutRequest;
import com.project.shopapp.model.request.RefreshRequest;
import com.project.shopapp.model.response.ApiResponse;
import com.project.shopapp.service.IOtpService;
import com.project.shopapp.service.IUserService;
import com.project.shopapp.utils.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Objects;

@RestController
@RequestMapping("${api.prefix}/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final JwtUtil jwtUtil;
    private final IUserService userService;
    private final IOtpService otpService;

    @PostMapping("/logout")
    public ResponseEntity<?> logoutToken(@RequestBody LogoutRequest logoutRequest)
            throws ParseException, JOSEException {
        jwtUtil.logout(logoutRequest);

        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Logout Token successfully!"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshRequest refreshRequest)
            throws ParseException, JOSEException {
        String refreshedToken = jwtUtil.refreshToken(refreshRequest);

        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Refresh token successfully!",
                refreshedToken));
    }

    @PostMapping("/forgot-password")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        userService.existByEmail(email);
        otpService.generateAndSendOtp(email);
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Otp was send to your email!"));
    }

    @PostMapping("/validate-otp")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> validateOTP(
            @RequestParam String email,
            @RequestParam String otpCode) {

        if (otpService.isValidOtp(email, otpCode)) {
            return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "OTP is valid!"));
        }

        return ResponseEntity.badRequest().body(new ApiResponse(HttpStatus.BAD_REQUEST.value(),
                "OTP is expired or invalid!"));
    }

    @PostMapping("/reset-password")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> resetPassword(
            @RequestParam String email,
            @RequestBody ChangePassword changePassword) {
        if (!Objects.equals(changePassword.getPassword(), changePassword.getRepeatPassword())) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(
                    new ApiResponse(HttpStatus.EXPECTATION_FAILED.value(), "Repeat password, please!"));
        }

        userService.updateByEmailAndPassword(email, changePassword);
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Reset password successfully!"));
    }
}
