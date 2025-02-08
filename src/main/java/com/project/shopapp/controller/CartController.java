package com.project.shopapp.controller;

import com.project.shopapp.model.request.ItemRequest;
import com.project.shopapp.model.response.ApiResponse;
import com.project.shopapp.model.response.PageResponse;
import com.project.shopapp.model.response.ProductResponse;
import com.project.shopapp.service.ICartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/carts")
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class CartController {
    private final ICartService cartService;

    @PostMapping("/{userId}/items")
    public ResponseEntity<?> addProductToCart(@PathVariable Long userId, @Valid @RequestBody ItemRequest itemRequest) {
        Long productId = itemRequest.getProductId();
        long quantity = itemRequest.getQuantity();
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Add product to cart successfully!",
                cartService.addProductToCart(userId, productId, quantity)));
    }

    @PutMapping("/{userId}/items")
    public ResponseEntity<?> updateProductInCart(@PathVariable Long userId, @Valid @RequestBody ItemRequest itemRequest) {
        Long productId = itemRequest.getProductId();
        long quantity = itemRequest.getQuantity();
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Update product in cart successfully!",
                cartService.updateProductInCart(userId, productId, quantity)));
    }

    @DeleteMapping("/{userId}/products/{productId}")
    public ResponseEntity<?> deleteProductFromCart(@PathVariable Long userId, @PathVariable Long productId) {
        cartService.removeProductFromCart(userId, productId);
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Delete product from cart successfully!"));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> clearAllProductsFromCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Clear all products from cart successfully!"));
    }

    @DeleteMapping("/{userId}/products")
    public ResponseEntity<?> removeProductListFromCart(@PathVariable Long userId,
                                                            @RequestBody List<Long> productList) {
        cartService.removeProductListFromCart(userId, productList);
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Delete products successfully!"));
    }

    @GetMapping("/{userId}/total-price")
    public ResponseEntity<?> getCartTotalPrice(@PathVariable Long userId) {
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Get cart's total price successfully!", cartService.calculateCartTotalPrice(userId)));
    }

    @GetMapping("/{userId}/total-product")
    public ResponseEntity<?> getCartTotalProduct(@PathVariable Long userId) {
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Get cart's total product successfully!",
                cartService.calculateCartTotalProduct(userId)));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getAllProductsFromCart(Pageable pageable, @PathVariable Long userId) {
        Page<ProductResponse> productResponsePage = cartService.getAllProductsFromCart(userId, pageable);
        List<ProductResponse> productResponses = productResponsePage.getContent();
        int totalPages = productResponsePage.getTotalPages();

        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Get all products from cart successfully!",
                PageResponse.builder()
                        .totalPages(totalPages)
                        .data(productResponses)
                        .build()));
    }

    @GetMapping
    public ResponseEntity<?> getMyCart() {
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Get my cart successfully!",
                cartService.getMyCart()));
    }
}
