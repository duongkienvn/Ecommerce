package com.project.shopapp.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.project.shopapp.entity.CartItemEntity;
import com.project.shopapp.entity.UserEntity;
import com.project.shopapp.utils.jackson.DoubleTwoDecimalSerializer;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponse extends BaseResponse {
    @JsonProperty("total_price")
    @JsonSerialize(using = DoubleTwoDecimalSerializer.class)
    Double totalPrice;

    @JsonProperty("total_items")
    Long totalItems;

    @JsonProperty("user_id")
    Long userId;

    List<CartItemResponse> cartItemResponses;
}
