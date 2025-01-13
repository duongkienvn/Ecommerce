package com.project.shopapp.service.impl;

import com.nimbusds.jose.KeyLengthException;
import com.project.shopapp.authentication.JwtService;
import com.project.shopapp.converter.UserConverter;
import com.project.shopapp.entity.RoleEntity;
import com.project.shopapp.entity.UserEntity;
import com.project.shopapp.exception.BadCredentialsException;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.exception.PermissionDenyException;
import com.project.shopapp.model.dto.UserDto;
import com.project.shopapp.model.dto.UserLoginDto;
import com.project.shopapp.repository.RoleRepository;
import com.project.shopapp.repository.UserRepostiory;
import com.project.shopapp.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserRepostiory userRepostiory;
    private final RoleRepository roleRepository;
    private final UserConverter userConverter;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public UserEntity createUser(UserDto userDto) {
        String phoneNumber = userDto.getPhoneNumber();

        if (userRepostiory.existsByPhoneNumber(phoneNumber)) {
            throw new DataIntegrityViolationException("Phone number has already existed!");
        }

        RoleEntity roleEntity = roleRepository.findById(userDto.getRoleId())
                .orElseThrow(() -> new DataNotFoundException("Role doesn't exist!"));
        if (roleEntity.getName().toUpperCase().equals(RoleEntity.ADMIN)) {
            throw new PermissionDenyException("You can't register an admin account!");
        }

        UserEntity newUser = userConverter.converToUser(userDto);

        newUser.setRoleEntity(roleEntity);

        if (userDto.getFacebookAccountId() == 0 && userDto.getGoogleAccountId() == 0) {
            String password = userDto.getPassword();
            newUser.setPassword(passwordEncoder.encode(password));
        }

        return userRepostiory.save(newUser);
    }

    @Override
    public String login(UserLoginDto userLoginDto) {
        String phoneNumber = userLoginDto.getPhoneNumber();
        String password = userLoginDto.getPassword();

        Optional<UserEntity> user = userRepostiory.findByPhoneNumber(phoneNumber);
        if (user.isEmpty()) {
            throw new DataNotFoundException("Invalid phonenumber or password!");
        }

        UserEntity existingUser = user.get();
        if (existingUser.getGoogleAccountId() == 0 && existingUser.getFacebookAccountId() == 0) {
            if (!passwordEncoder.matches(password, existingUser.getPassword())) {
                throw new BadCredentialsException("Phone number or password is wrong!");
            }
        }

        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(phoneNumber, password);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        if (authentication.isAuthenticated()) {
            try {
                return jwtService.generateToken(phoneNumber);
            } catch (KeyLengthException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }
}
