package com.project.shopapp.controller;

import com.nimbusds.jose.JOSEException;
import com.project.shopapp.model.request.LogoutRequest;
import com.project.shopapp.model.request.RefreshRequest;
import com.project.shopapp.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("${api.prefix}/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final JwtUtil jwtUtil;

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
}
