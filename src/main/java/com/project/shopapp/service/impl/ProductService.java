package com.project.shopapp.service.impl;

import com.project.shopapp.entity.CategoryEntity;
import com.project.shopapp.entity.ProductEntity;
import com.project.shopapp.entity.ProductImageEntity;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.exception.InvalidParamException;
import com.project.shopapp.model.dto.ProductDto;
import com.project.shopapp.model.dto.ProductImageDto;
import com.project.shopapp.model.response.ProductResponse;
import com.project.shopapp.repository.CategoryRepository;
import com.project.shopapp.repository.ProductImageRepository;
import com.project.shopapp.repository.ProductRepository;
import com.project.shopapp.service.ICategoryService;
import com.project.shopapp.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;

    @Override
    public ProductEntity createProduct(ProductDto productDto) {
        CategoryEntity existingCategory = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find category with id: " + productDto.getCategoryId()));

        ProductEntity newProduct = ProductEntity.builder()
                .name(productDto.getName())
                .price(productDto.getPrice())
                .description(productDto.getDescription())
                .thumbnail(productDto.getThumbnail())
                .category(existingCategory)
                .build();

        return productRepository.save(newProduct);
    }

    @Override
    public ProductEntity getProductById(Long id) {
        return productRepository
                .findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find product with id: " + id));
    }

    @Override
    public ProductEntity updateProduct(Long id, ProductDto productDto) {
        ProductEntity existingProduct = getProductById(id);
        CategoryEntity existingCategory = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find category with id: " + productDto.getCategoryId()));

        existingProduct.setName(productDto.getName());
        existingProduct.setPrice(productDto.getPrice());
        existingProduct.setDescription(productDto.getDescription());
        existingProduct.setThumbnail(productDto.getThumbnail());
        existingProduct.setCategory(existingCategory);

        return productRepository.save(existingProduct);
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
    public ProductImageEntity createProductImage(ProductImageDto productImageDto) throws InvalidParamException {
        ProductEntity existingProduct = getProductById(productImageDto.getProductId());
        List<ProductImageEntity> productImageEntities
                = productImageRepository.getProductImageEntitiesByProductEntityId(productImageDto.getProductId());

        if (productImageEntities.size() > ProductImageEntity.MAXIMUM_IMAGES_PER_PRODUCT) {
            throw new InvalidParamException("The number of image must <= "
                    + ProductImageEntity.MAXIMUM_IMAGES_PER_PRODUCT);
        }

        ProductImageEntity newProductImage = ProductImageEntity.builder()
                .imageUrl(productImageDto.getImageUrl())
                .productEntity(existingProduct)
                .build();

        return productImageRepository.save(newProductImage);
    }
}
