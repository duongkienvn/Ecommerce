package com.project.shopapp.service.impl;

import com.project.shopapp.authentication.AuthenticationFacade;
import com.project.shopapp.converter.CartConverter;
import com.project.shopapp.entity.CartEntity;
import com.project.shopapp.entity.CartItemEntity;
import com.project.shopapp.entity.ProductEntity;
import com.project.shopapp.entity.UserEntity;
import com.project.shopapp.exception.AppException;
import com.project.shopapp.exception.ErrorCode;
import com.project.shopapp.model.response.CartResponse;
import com.project.shopapp.model.response.ProductResponse;
import com.project.shopapp.repository.CartItemRepository;
import com.project.shopapp.repository.CartRepository;
import com.project.shopapp.service.ICartService;
import com.project.shopapp.service.IProductService;
import com.project.shopapp.service.IUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CartService implements ICartService {
    private final CartRepository cartRepository;
    private final IUserService userService;
    private final IProductService productService;
    private final CartConverter cartConverter;
    private final CartItemRepository cartItemRepository;
    private final AuthenticationFacade authenticationFacade;

    @Override
    public CartResponse addProductToCart(Long userId, Long productId, long quantity) {
        ProductEntity existingProduct = productService.getProductById(productId);
        UserEntity existingUser = userService.getById(userId);

        CartItemEntity cartItem = CartItemEntity.builder()
                .product(existingProduct)
                .PricePerUnit(existingProduct.getPrice())
                .totalPrice(quantity * existingProduct.getPrice())
                .quantity(quantity)
                .build();

        CartEntity cart = getCartByUserId(userId);
        List<CartItemEntity> cartItemEntities = cart.getCartItemEntities();
        boolean check = isExistProduct(cartItemEntities, cart.getId(), productId, cartItem);

        if (!check) {
            cartItemEntities.add(cartItem);
        }

        calculateCartTotals(cart);
        cart.setUser(existingUser);
        cart.setCartItemEntities(cartItemEntities);

        if (!check) {
            cartItem.setCart(cart);
        }
        cartRepository.save(cart);

        return cartConverter.convertToCartResponse(cart);
    }

    private boolean isExistProduct(List<CartItemEntity> cartItemEntities, Long cartId,
                                   Long productId, CartItemEntity cartItem) {
        for (CartItemEntity cartItemEntity : cartItemEntities) {
            if (cartItemEntity.getCart().getId().equals(cartId)) {
                if (cartItemEntity.getProduct().getId().equals(productId)) {
                    long oldQuantity = cartItemEntity.getQuantity();
                    float oldTotalPrice = cartItemEntity.getTotalPrice();
                    cartItemEntity.setQuantity(oldQuantity + cartItem.getQuantity());
                    cartItemEntity.setTotalPrice(oldTotalPrice + cartItem.getTotalPrice());
                    return true;
                }
            }
        }

        return false;
    }

    private void calculateCartTotals(CartEntity cart) {
        List<CartItemEntity> cartItemEntities = cart.getCartItemEntities();

        double totalPrice = cartItemEntities.stream()
                .mapToDouble(CartItemEntity::getTotalPrice)
                .sum();

        long totalItems = cartItemEntities.stream()
                .mapToLong(CartItemEntity::getQuantity)
                .sum();

        cart.setTotalPrice(totalPrice);
        cart.setTotalItems(totalItems);
    }

    @Override
    public CartResponse updateProductInCart(Long userId, Long productId, long quantity) {
        ProductEntity existingProduct = productService.getProductById(productId);
        UserEntity existingUser = userService.getById(userId);

        CartEntity cart = getCartByUserId(userId);
        CartItemEntity cartItem = cartItemRepository.getByCartIdAndProductId(cart.getId(), productId);
        if (cartItem == null) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        long oldQuantity = cartItem.getQuantity();
        float oldTotalPrice = cartItem.getTotalPrice();
        long newQuantity = quantity;
        float newTotalPrice = quantity * existingProduct.getPrice();

        cartItem.setQuantity(oldQuantity + newQuantity);
        cartItem.setTotalPrice(oldTotalPrice + newTotalPrice);

        cart.setTotalItems(cart.getTotalItems() + newQuantity);
        cart.setTotalPrice(cart.getTotalPrice() + newTotalPrice);
        cartRepository.save(cart);

        return cartConverter.convertToCartResponse(cart);
    }

    @Override
    @Transactional
    public void removeProductFromCart(Long userId, Long productId) {
        CartEntity cart = getCartByUserId(userId);
        List<CartItemEntity> cartItemEntities = cart.getCartItemEntities();
        CartItemEntity cartItem = cartItemRepository.getByCartIdAndProductId(cart.getId(), productId);
        if (cartItem == null) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        cart.setTotalItems(cart.getTotalItems() - cartItem.getQuantity());
        cart.setTotalPrice(cart.getTotalPrice() - cartItem.getTotalPrice());

        cartItemEntities.remove(cartItem);
        cartRepository.save(cart);
    }

    @Override
    public CartEntity getCartByUserId(Long userId) {
        Optional<CartEntity> optionalCart = cartRepository.findByUserId(userId);
        if (optionalCart.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        return optionalCart.get();
    }

    @Override
    public double calculateCartTotalPrice(Long userId) {
        CartEntity cart = getCartByUserId(userId);
        return cart.getTotalPrice();
    }

    @Override
    public void clearCart(Long userId) {
        CartEntity cart = getCartByUserId(userId);
        List<CartItemEntity> cartItemEntities = cart.getCartItemEntities();
        cartItemEntities.clear();
        cart.setTotalPrice(0D);
        cart.setTotalItems(0L);
        cartRepository.save(cart);
    }

    @Override
    public Page<ProductResponse> getAllProductsFromCart(Long userId, PageRequest pageRequest) {
        CartEntity cart = getCartByUserId(userId);
        Page<CartItemEntity> cartItemEntityPage = cartItemRepository.getByCartId(cart.getId(), pageRequest);
        Page<ProductResponse> productResponsePage = cartItemEntityPage
                .map(cartItem -> ProductResponse.fromProduct(cartItem.getProduct()));

        return productResponsePage;
    }

    @Override
    @Transactional
    public void removeProductListFromCart(Long userId, List<Long> productIdList) {
        CartEntity cart = getCartByUserId(userId);
        List<CartItemEntity> cartItemEntities = cart.getCartItemEntities();
        List<CartItemEntity> removedCartEntities
                = cartItemRepository.getByCartIdAndProductIdIn(cart.getId(), productIdList);

        long removedQuantity = 0;
        float removedTotalPrice = 0;

        for (CartItemEntity cartItem : removedCartEntities) {
            removedQuantity += cartItem.getQuantity();
            removedTotalPrice += cartItem.getTotalPrice();
            cartItemEntities.remove(cartItem);
        }
        cart.setTotalItems(cart.getTotalItems() - removedQuantity);
        cart.setTotalPrice(cart.getTotalPrice() - removedTotalPrice);

        cartRepository.save(cart);
    }

    @Override
    public long calculateCartTotalProduct(Long userId) {
        CartEntity cart = getCartByUserId(userId);
        return cart.getTotalItems();
    }

    @Override
    public CartResponse getMyCart() {
        String phoneNumber = authenticationFacade.getCurrentName();
        CartEntity cart = cartRepository.findByUser_PhoneNumber(phoneNumber);

        return cartConverter.convertToCartResponse(cart);
    }

    private List<CartItemEntity> getCartItemListByUserId(Long userId) {
        CartEntity cart = getCartByUserId(userId);
        List<CartItemEntity> cartItemEntities = cart.getCartItemEntities();

        return cartItemEntities;
    }
}
