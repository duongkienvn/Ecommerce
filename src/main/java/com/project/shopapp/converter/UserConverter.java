package com.project.shopapp.converter;

import com.project.shopapp.entity.UserEntity;
import com.project.shopapp.model.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConverter {
    private final ModelMapper modelMapper;

    public UserEntity converToUser(UserDto userDto) {
        UserEntity user = UserEntity.builder()
                .fullName(userDto.getFullName())
                .phoneNumber(userDto.getPhoneNumber())
                .address(userDto.getAddress())
                .dateOfBirth(userDto.getDateOfBirth())
                .facebookAccountId(userDto.getFacebookAccountId())
                .googleAccountId(userDto.getGoogleAccountId())
                .active(1)
                .password(userDto.getPassword())
                .build();
        return user;
    }
}
