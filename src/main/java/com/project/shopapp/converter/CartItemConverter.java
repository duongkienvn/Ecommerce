package com.project.shopapp.converter;

import com.project.shopapp.entity.CartItemEntity;
import com.project.shopapp.model.response.CartItemResponse;
import org.springframework.stereotype.Component;

@Component
public class CartItemConverter {
    public CartItemResponse convertToCartItemResponse(CartItemEntity cartItem) {
        return CartItemResponse.builder()
                .PricePerUnit(cartItem.getPricePerUnit())
                .productId(cartItem.getProduct().getId())
                .quantity(cartItem.getQuantity())
                .totalPrice(cartItem.getTotalPrice())
                .build();
    }
}
