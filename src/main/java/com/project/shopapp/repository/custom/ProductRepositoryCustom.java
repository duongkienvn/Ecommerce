package com.project.shopapp.repository.custom;

import com.project.shopapp.entity.ProductEntity;
import com.project.shopapp.model.request.ProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductRepositoryCustom {
    Page<ProductEntity> findProduct(ProductRequest productRequest, Pageable pageable);
}
