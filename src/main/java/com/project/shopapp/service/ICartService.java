package com.project.shopapp.service;

import com.project.shopapp.entity.CartEntity;
import com.project.shopapp.model.response.CartResponse;
import com.project.shopapp.model.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICartService {
    CartResponse addProductToCart(Long userId, Long productId, long quantity);
    void removeProductFromCart(Long userId, Long productId);
    CartEntity getCartByUserId(Long userId);
    CartResponse updateProductInCart(Long userId, Long productId, long quantity);
    double calculateCartTotalPrice(Long userId);
    void clearCart(Long userId);
    Page<ProductResponse> getAllProductsFromCart(Long userId, Pageable pageable);
    void removeProductListFromCart(Long userId, List<Long> productIdList);
    long calculateCartTotalProduct(Long userId);
    CartResponse getMyCart();
}
