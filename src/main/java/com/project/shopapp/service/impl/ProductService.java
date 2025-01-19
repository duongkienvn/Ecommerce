package com.project.shopapp.service.impl;

import com.project.shopapp.converter.ProductRequestMapper;
import com.project.shopapp.entity.CategoryEntity;
import com.project.shopapp.entity.ProductEntity;
import com.project.shopapp.entity.ProductImageEntity;
import com.project.shopapp.exception.AppException;
import com.project.shopapp.exception.ErrorCode;
import com.project.shopapp.exception.InvalidParamException;
import com.project.shopapp.model.dto.ProductDto;
import com.project.shopapp.model.dto.ProductImageDto;
import com.project.shopapp.model.request.ProductRequest;
import com.project.shopapp.model.response.ProductImageResponse;
import com.project.shopapp.model.response.ProductResponse;
import com.project.shopapp.repository.CategoryRepository;
import com.project.shopapp.repository.ProductImageRepository;
import com.project.shopapp.repository.ProductRepository;
import com.project.shopapp.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;

    @Override
    public ProductResponse createProduct(ProductDto productDto) {
        CategoryEntity existingCategory = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        ProductEntity newProduct = ProductEntity.builder()
                .name(productDto.getName())
                .price(productDto.getPrice())
                .description(productDto.getDescription())
                .thumbnail(productDto.getThumbnail())
                .category(existingCategory)
                .build();
        productRepository.save(newProduct);

        return ProductResponse.fromProduct(newProduct);
    }

    @Override
    public ProductEntity getProductById(Long id) {
        ProductEntity existingProduct = productRepository
                .findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return existingProduct;
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductDto productDto) {
        ProductEntity existingProduct = getProductById(id);
        CategoryEntity existingCategory = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        existingProduct.setName(productDto.getName());
        existingProduct.setPrice(productDto.getPrice());
        existingProduct.setDescription(productDto.getDescription());
        existingProduct.setThumbnail(productDto.getThumbnail());
        existingProduct.setCategory(existingCategory);

        productRepository.save(existingProduct);
        return ProductResponse.fromProduct(existingProduct);
    }

    @Override
    public Page<ProductResponse> getAllProducts(PageRequest pageRequest) {
        return productRepository
                .findAll(pageRequest)
                .map(ProductResponse::fromProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        ProductEntity productEntity = getProductById(id);
        productRepository.delete(productEntity);
    }

    @Override
    public ProductImageResponse createProductImage(ProductImageDto productImageDto) {
        ProductEntity existingProduct = getProductById(productImageDto.getProductId());
        List<ProductImageEntity> productImageEntities
                = productImageRepository.getProductImageEntitiesByProductEntityId(productImageDto.getProductId());

        if (productImageEntities.size() > ProductImageEntity.MAXIMUM_IMAGES_PER_PRODUCT) {
            throw new InvalidParamException();
        }

        ProductImageEntity newProductImage = ProductImageEntity.builder()
                .imageUrl(productImageDto.getImageUrl())
                .productEntity(existingProduct)
                .build();
        productImageRepository.save(newProductImage);

        return ProductImageResponse.builder()
                .imageUrl(newProductImage.getImageUrl())
                .productId(newProductImage.getProductEntity().getId())
                .build();
    }

    @Override
    public Page<ProductResponse> findProduct(Map<String, Object> productMap, PageRequest pageRequest) {
        ProductRequest productRequest = ProductRequestMapper.toProductRequest(productMap);
        Page<ProductEntity> productEntities = productRepository.findProduct(productRequest, pageRequest);

        List<ProductResponse> productResponses = productEntities
                .stream()
                .map(ProductResponse::fromProduct)
                .toList();

        return new PageImpl<>(productResponses, pageRequest, productEntities.getTotalElements());
    }
}
