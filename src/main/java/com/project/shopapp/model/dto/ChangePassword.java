package com.project.shopapp.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePassword {
    @NotBlank(message = "Password mustn't blank!")
    private String password;

    @NotBlank(message = "Repeat Password mustn't blank!")
    private String repeatPassword;
}
