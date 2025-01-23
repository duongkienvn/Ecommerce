package com.project.shopapp.service.impl;

import com.nimbusds.jose.KeyLengthException;
import com.project.shopapp.exception.*;
import com.project.shopapp.model.response.UserResponse;
import com.project.shopapp.utils.JwtUtil;
import com.project.shopapp.converter.UserConverter;
import com.project.shopapp.entity.RoleEntity;
import com.project.shopapp.entity.UserEntity;
import com.project.shopapp.model.dto.UserDto;
import com.project.shopapp.model.dto.UserLoginDto;
import com.project.shopapp.repository.RoleRepository;
import com.project.shopapp.repository.UserRepostiory;
import com.project.shopapp.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepostiory userRepostiory;
    private final RoleRepository roleRepository;
    private final UserConverter userConverter;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public UserResponse createUser(UserDto userDto) {
        String phoneNumber = userDto.getPhoneNumber();

        if (userRepostiory.existsByPhoneNumber(phoneNumber)) {
            throw new AppException(ErrorCode.DATA_INTEGRITY_VIOLATION);
        }

        RoleEntity roleEntity = roleRepository.findById(userDto.getRoleId())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        if (roleEntity.getName().toUpperCase().equals(RoleEntity.ADMIN)) {
            throw new AppException(ErrorCode.PERMISSION_DENY);
        }

        UserEntity newUser = userConverter.converToUser(userDto);

        newUser.setRoleEntity(roleEntity);

        if (userDto.getFacebookAccountId() == 0 && userDto.getGoogleAccountId() == 0) {
            String password = userDto.getPassword();
            newUser.setPassword(passwordEncoder.encode(password));
        }
        userRepostiory.save(newUser);

        return userConverter.convertToUserResponse(newUser);
    }

    @Override
    public String login(UserLoginDto userLoginDto) {
        String phoneNumber = userLoginDto.getPhoneNumber();
        String password = userLoginDto.getPassword();

        Optional<UserEntity> user = userRepostiory.findByPhoneNumber(phoneNumber);
        if (user.isEmpty()) {
            throw new AppException(ErrorCode.DATA_NOT_FOUND);
        }

        UserEntity existingUser = user.get();
        if (existingUser.getGoogleAccountId() == 0 && existingUser.getFacebookAccountId() == 0) {
            if (!passwordEncoder.matches(password, existingUser.getPassword())) {
                throw new AppException(ErrorCode.BAD_CREDENTIALS);
            }
        }

        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(phoneNumber, password);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        if (authentication.isAuthenticated()) {
            try {
                return jwtUtil.generateToken(existingUser);
            } catch (KeyLengthException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    @Override
    public UserResponse getUserByPhoneNumber(String phonenumber) {
        UserEntity existingUser = userRepostiory.findByPhoneNumber(phonenumber)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userConverter.convertToUserResponse(existingUser);
    }

    @Override
    public Page<UserResponse> findAllUsers(PageRequest pageRequest) {
        Page<UserEntity> userEntities = userRepostiory.findAll(pageRequest);

        return userEntities.map(user -> userConverter.convertToUserResponse(user));
    }
}
