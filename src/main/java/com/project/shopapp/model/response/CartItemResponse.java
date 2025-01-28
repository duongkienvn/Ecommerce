package com.project.shopapp.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.entity.CartEntity;
import com.project.shopapp.entity.ProductEntity;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemResponse {
    Long quantity;

    @JsonProperty("price_per_unit")
    Float PricePerUnit;

    @JsonProperty("total_price")
    Float totalPrice;

    @JsonProperty("product_id")
    Long productId;
}
