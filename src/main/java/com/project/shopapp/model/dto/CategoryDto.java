package com.project.shopapp.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoryDto {
    @NotBlank(message = "Category's name cannot be blank!")
    private String name;
}
