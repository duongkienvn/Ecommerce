package com.project.shopapp.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemRequest {
    @Min(value = 1, message = "Product's Id must be >= 1")
    @JsonProperty("product_id")
    private Long productId;

    @Min(value = 1, message = "Product's quantity must be >= 1")
    private long quantity;
}
