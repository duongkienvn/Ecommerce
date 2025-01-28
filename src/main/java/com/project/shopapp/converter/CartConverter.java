package com.project.shopapp.converter;

import com.project.shopapp.entity.CartEntity;
import com.project.shopapp.model.response.CartItemResponse;
import com.project.shopapp.model.response.CartResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CartConverter {
    private final CartItemConverter cartItemConverter;

    public CartResponse convertToCartResponse(CartEntity cart) {
        List<CartItemResponse> cartItemResponseList = cart.getCartItemEntities()
                .stream()
                .map(cartItem -> cartItemConverter.convertToCartItemResponse(cartItem))
                .toList();

        return CartResponse.builder()
                .userId(cart.getUser().getId())
                .totalPrice(cart.getTotalPrice())
                .totalItems(cart.getTotalItems())
                .cartItemResponses(cartItemResponseList)
                .build();
    }
}
