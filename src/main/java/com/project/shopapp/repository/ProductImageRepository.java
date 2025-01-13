package com.project.shopapp.repository;

import com.project.shopapp.entity.ProductImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImageEntity, Long> {
    List<ProductImageEntity> getProductImageEntitiesByProductEntityId(Long productId);
}
