package com.project.shopapp.service.impl;

import com.nimbusds.jose.JOSEException;
import com.project.shopapp.exception.AppException;
import com.project.shopapp.model.request.IntrospectRequest;
import com.project.shopapp.model.response.IntrospectResponse;
import com.project.shopapp.utils.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtUtil jwtUtil;

    public IntrospectResponse introspect(IntrospectRequest introspectRequest) throws ParseException, JOSEException {
        String token = introspectRequest.getToken();
        boolean isValid = true;

        try {
            jwtUtil.verifyToken(token);
        } catch (AppException e) {
            isValid = false;
        }

        return IntrospectResponse.builder()
                .isValid(isValid)
                .build();
    }
}
