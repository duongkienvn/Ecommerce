package com.project.shopapp.service;

import com.nimbusds.jose.JOSEException;
import com.project.shopapp.exception.UnauthenticationException;
import com.project.shopapp.model.request.IntrospectRequest;
import com.project.shopapp.model.response.IntrospectResponse;
import com.project.shopapp.utils.JwtUtil;
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
        } catch (UnauthenticationException e) {
            isValid = false;
        }

        return IntrospectResponse.builder()
                .isValid(isValid)
                .build();
    }
}
