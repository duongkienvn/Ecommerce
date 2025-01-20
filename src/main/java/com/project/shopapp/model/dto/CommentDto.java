package com.project.shopapp.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDto {
    @NotBlank(message = "Content mustn't blank")
    private String content;

    @JsonProperty("user_id")
    @NotNull(message = "User's id cannot be null")
    @Min(value = 1, message = "User's id must be >= 1")
    private Long userId;

    @JsonProperty("product_id")
    @NotNull(message = "Product's id cannot be null")
    @Min(value = 1, message = "Product's id must be >= 1")
    private Long productId;
}
