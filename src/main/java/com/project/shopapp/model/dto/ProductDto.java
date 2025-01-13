package com.project.shopapp.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDto {
    @NotBlank(message = "Product's name is required!")
    @Size(min = 3, max = 100, message = "Product's name must be between 3 and 100 characters!")
    String name;

    @Min(value = 0, message = "Product's price must be greater than or equal 0")
    @Max(value = 1000000000, message = "Product's price must be less than or equal 1000000000")
    Float price;

    String thumbnail;
    String description;

    @JsonProperty("category_id")
    Long categoryId;

    List<MultipartFile> files;
}
