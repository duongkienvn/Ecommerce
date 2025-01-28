package com.project.shopapp.controller;

import com.nimbusds.jose.JOSEException;
import com.project.shopapp.model.dto.ChangePassword;
import com.project.shopapp.model.request.LogoutRequest;
import com.project.shopapp.model.request.RefreshRequest;
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
    public ResponseEntity<String> logoutToken(@RequestBody LogoutRequest logoutRequest)
            throws ParseException, JOSEException {
        jwtUtil.logout(logoutRequest);

        return ResponseEntity.ok("Logout Token successfully!");
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshToken(@RequestBody RefreshRequest refreshRequest)
            throws ParseException, JOSEException {
        String refreshedToken = jwtUtil.refreshToken(refreshRequest);

        return ResponseEntity.ok(refreshedToken);
    }

    @PostMapping("/forgot-password")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        userService.existByEmail(email);
        otpService.generateAndSendOtp(email);
        return ResponseEntity.ok("Otp was send to your email!");
    }

    @PostMapping("/validate-otp")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<String> validateOtp(
            @RequestParam String email,
            @RequestParam String otpCode) {

        if (otpService.isValidOtp(email, otpCode)) {
            return ResponseEntity.ok("OTP is valid!");
        }

        return ResponseEntity.badRequest().body("OTP is expired or invalid!");
    }

    @PostMapping("/reset-password")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> resetPassword(
            @RequestParam String email,
            @RequestBody ChangePassword changePassword) {
        if (!Objects.equals(changePassword.getPassword(), changePassword.getRepeatPassword())) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Please enter the password again!");
        }

        userService.updateByEmailAndPassword(email, changePassword);
        return ResponseEntity.ok("Reset password successfully!");
    }
}
