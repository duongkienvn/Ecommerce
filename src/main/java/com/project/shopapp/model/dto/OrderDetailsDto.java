package com.project.shopapp.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailsDto {
    @JsonProperty("order_id")
    @Min(value = 1, message = "Order's id must be > 0")
    Long orderId;

    @JsonProperty("product_id")
    @Min(value = 1, message = "Product's id must be > 0")
    Long productId;

    @Min(value = 0, message = "The price must be > 0")
    Float price;

    @JsonProperty("number_of_products")
    @Min(value = 1, message = "The number of product must be >= 1")
    Integer numberOfProducts;

    @JsonProperty("total_money")
    @Min(value = 0, message = "The total money must be >= 0")
    Float totalMoney;

    String color;
}
