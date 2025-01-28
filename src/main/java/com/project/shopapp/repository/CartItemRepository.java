package com.project.shopapp.repository;

import com.project.shopapp.entity.CartItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
    CartItemEntity getByCartIdAndProductId(Long cartId, Long ProductId);
    List<CartItemEntity> getByCartIdAndProductIdIn(Long cartId, List<Long> productIdList);
    Page<CartItemEntity> getByCartId(Long cartId, Pageable pageable);
}
