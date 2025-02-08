package com.project.shopapp.service.impl;

import com.nimbusds.jose.KeyLengthException;
import com.project.shopapp.authentication.AuthenticationFacade;
import com.project.shopapp.entity.CartEntity;
import com.project.shopapp.exception.*;
import com.project.shopapp.model.dto.ChangePassword;
import com.project.shopapp.model.request.UserUpdateRequest;
import com.project.shopapp.model.response.UserResponse;
import com.project.shopapp.repository.CartRepository;
import com.project.shopapp.utils.jwt.JwtUtil;
import com.project.shopapp.converter.UserConverter;
import com.project.shopapp.entity.RoleEntity;
import com.project.shopapp.entity.UserEntity;
import com.project.shopapp.model.dto.UserDto;
import com.project.shopapp.model.dto.UserLoginDto;
import com.project.shopapp.repository.RoleRepository;
import com.project.shopapp.repository.UserRepostiory;
import com.project.shopapp.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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
    private final AuthenticationFacade authenticationFacade;
    private final CartRepository cartRepository;

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

        CartEntity cart = CartEntity.builder()
                .totalItems(0L)
                .totalPrice(0D)
                .user(newUser)
                .build();
        newUser.setCartEntity(cart);
        userRepostiory.save(newUser);

        return userConverter.convertToUserResponse(newUser);
    }

    @Override
    public String login(UserLoginDto userLoginDto) {
        String phoneNumber = userLoginDto.getPhoneNumber();
        String password = userLoginDto.getPassword();

        Optional<UserEntity> user = userRepostiory.findByPhoneNumber(phoneNumber);
        if (user.isEmpty() || user.get().getActive() == 0) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
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

        if (existingUser.getActive() == 0) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        return userConverter.convertToUserResponse(existingUser);
    }

    @Override
    public Page<UserResponse> findAllUsers(Pageable pageable) {
        Page<UserEntity> userEntities = userRepostiory.findAll(pageable);
        return userEntities.map(user -> userConverter.convertToUserResponse(user));
    }

    @Override
    public void existByEmail(String email) {
        boolean emailExists = userRepostiory.existsByEmail(email);
        if (!emailExists) {
            throw new AppException(ErrorCode.EMAIL_NOT_FOUND);
        }
    }

    @Override
    public void updateByEmailAndPassword(String email, ChangePassword changePassword) {
        if (!Objects.equals(changePassword.getPassword(), changePassword.getRepeatPassword())) {
            throw new AppException(ErrorCode.UNMATCHED_PASSWORD);
        }
        userRepostiory.updateByEmailAndPassword(email, passwordEncoder.encode(changePassword.getPassword()));
    }

    @Override
    public void changePassword(Long userId, String currentPassword, ChangePassword changePassword) {
        UserEntity user = getById(userId);
        String password = user.getPassword();
        if (!passwordEncoder.matches(currentPassword, password)) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }
        if (!Objects.equals(changePassword.getPassword(), changePassword.getRepeatPassword())) {
            throw new AppException(ErrorCode.UNMATCHED_PASSWORD);
        }

        userRepostiory.updateById(userId, passwordEncoder.encode(changePassword.getPassword()));
    }

    @Override
    public UserEntity getById(Long id) {
        Optional<UserEntity> optionalUser = userRepostiory
                .findById(id);

        if (optionalUser.isEmpty() || optionalUser.get().getActive() == 0) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        return optionalUser.get();
    }

    @Override
    public void deleteUser(Long userId) {
        UserEntity existingUser = getById(userId);

        List<String> roles = authenticationFacade.getCurrentRoles();
        String name = authenticationFacade.getCurrentName();

        if (!(roles.contains("ROLE_ADMIN") || name.equals(existingUser.getPhoneNumber()))) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        existingUser.setActive(0);
        userRepostiory.save(existingUser);
    }

    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest userUpdateRequest) {
        UserEntity exisitingUser = getById(id);
        exisitingUser.setFullName(userUpdateRequest.getFullName());
        exisitingUser.setAddress(userUpdateRequest.getAddress());
        exisitingUser.setEmail(userUpdateRequest.getEmail());
        exisitingUser.setDateOfBirth(userUpdateRequest.getDateOfBirth());
        exisitingUser.setFacebookAccountId(userUpdateRequest.getFacebookAccountId());
        exisitingUser.setGoogleAccountId(userUpdateRequest.getGoogleAccountId());
        exisitingUser.setPhoneNumber(userUpdateRequest.getPhoneNumber());

        userRepostiory.save(exisitingUser);
        return userConverter.convertToUserResponse(exisitingUser);
    }

    @Override
    public UserResponse getMyInfo() {
        String phoneNumber = authenticationFacade.getCurrentName();
        return getUserByPhoneNumber(phoneNumber);
    }
}
