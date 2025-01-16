package com.project.shopapp.service;

import com.project.shopapp.entity.ProductEntity;
import com.project.shopapp.entity.ProductImageEntity;
import com.project.shopapp.exception.InvalidParamException;
import com.project.shopapp.model.dto.ProductDto;
import com.project.shopapp.model.dto.ProductImageDto;
import com.project.shopapp.model.request.ProductRequest;
import com.project.shopapp.model.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Map;

public interface IProductService {
    ProductEntity createProduct(ProductDto productDto);
    ProductEntity getProductById(Long id);
    ProductEntity updateProduct(Long id, ProductDto productDto);
    Page<ProductResponse> getAllProducts(PageRequest pageRequest);
    void deleteProduct(Long id);
    ProductImageEntity createProductImage(ProductImageDto productImageDto) throws Exception;
    Page<ProductResponse> findProduct(Map<String, Object> productMap, PageRequest pageRequest);
}
