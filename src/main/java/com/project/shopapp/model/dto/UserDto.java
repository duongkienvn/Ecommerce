package com.project.shopapp.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    @JsonProperty("fullname")
    String fullName;

    @JsonProperty("phone_number")
    @NotBlank(message = "Phone number is required!")
    String phoneNumber;

    String address;

    @NotBlank(message = "Password can not be blank!")
    String password;

    @JsonProperty("retype_password")
    String retypePassword;

    @JsonProperty("date_of_birth")
    Date dateOfBirth;

    @JsonProperty("facebook_account_id")
    int facebookAccountId;

    @JsonProperty("google_account_id")
    int googleAccountId;

    @JsonProperty("role_id")
    @NotNull(message = "Role is required!")
    Long roleId;

    @Email(message = "Email does not correct pattern!")
    @NotBlank(message = "Email must not be blank!")
    String email;
}
