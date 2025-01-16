package com.project.shopapp.utils;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.project.shopapp.entity.InvalidatedTokenEntity;
import com.project.shopapp.entity.UserEntity;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.exception.UnauthenticationException;
import com.project.shopapp.model.request.LogoutRequest;
import com.project.shopapp.model.request.RefreshRequest;
import com.project.shopapp.repository.InvalidatedTokenRepository;
import com.project.shopapp.repository.UserRepostiory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    @Value("${jwt.expiration}")
    private int expiration;

    @Value("${jwt.secretKey}")
    private String secretKey;

    private final InvalidatedTokenRepository invalidatedTokenRepository;
    private final UserRepostiory userRepostiory;

    public String generateToken(UserEntity user) throws KeyLengthException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getPhoneNumber())
                .issuer("admin")
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + expiration))
                .claim("scope", buildScope(user))
                .jwtID(UUID.randomUUID().toString())
                .build();

        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject object = new JWSObject(header, payload);

        try {
            object.sign(new MACSigner(secretKey.getBytes()));
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }

        return object.serialize();
    }

    public String buildScope(UserEntity user) {
        return "ROLE_" + user.getRoleEntity().getName().toUpperCase();
    }

    public SignedJWT verifyToken(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(secretKey.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiredTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        boolean verified = signedJWT.verify(verifier);

        if (!(verified && expiredTime.after(new Date()))) {
            throw new UnauthenticationException("Unauthenticated!");
        }

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new UnauthenticationException("Unauthenticated!");
        }

        return signedJWT;
    }

    public void logout(LogoutRequest logoutRequest) throws ParseException, JOSEException {
        SignedJWT signedJWT = verifyToken(logoutRequest.getToken());

        String jit = signedJWT.getJWTClaimsSet().getJWTID();
        Date expiredTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedTokenEntity invalidatedToken = InvalidatedTokenEntity
                .builder()
                .id(jit)
                .expiredTime(expiredTime)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);
    }

    public String refreshToken(RefreshRequest request)
            throws ParseException, JOSEException {
        SignedJWT signedJWT = verifyToken(request.getToken());

        String jit = signedJWT.getJWTClaimsSet().getJWTID();
        Date expiredTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedTokenEntity invalidatedToken = InvalidatedTokenEntity.builder()
                .id(jit)
                .expiredTime(expiredTime)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);

        String phoneNumber = signedJWT.getJWTClaimsSet().getSubject();
        UserEntity user = userRepostiory
                .findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UnauthenticationException("Unauthenticated!"));

        String newToken = generateToken(user);

        return newToken;
    }
}
