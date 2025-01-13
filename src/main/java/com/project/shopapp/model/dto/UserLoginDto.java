package com.project.shopapp.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserLoginDto {
    @JsonProperty("phone_number")
    @NotBlank(message = "Phone number is required!")
    String phoneNumber;

    @NotBlank(message = "Password is required!")
    String password;
}
