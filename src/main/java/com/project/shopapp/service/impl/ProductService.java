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
import com.project.shopapp.model.response.PageResponse;
import com.project.shopapp.model.response.ProductImageResponse;
import com.project.shopapp.model.response.ProductResponse;
import com.project.shopapp.repository.CategoryRepository;
import com.project.shopapp.repository.ProductImageRepository;
import com.project.shopapp.repository.ProductRepository;
import com.project.shopapp.service.IProductService;
import com.project.shopapp.specification.ProductSpecs;
import com.project.shopapp.utils.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final BaseRedisService baseRedisService;
    private static final String PRODUCT_CACHE_PREFIX = "product:";

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

        ProductResponse productResponse = ProductResponse.fromProduct(newProduct);

        return productResponse;
    }

    @Override
    public ProductEntity getProductEntityById(Long id) {
        ProductEntity existingProduct = productRepository
                .findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return existingProduct;
    }

    @Override
    public ProductResponse getProductById(Long id) {
        String cachedKey = PRODUCT_CACHE_PREFIX + id;
        Object cachedData = baseRedisService.get(cachedKey);

        if (Objects.nonNull(cachedData)) {
            ProductResponse cachedProduct = RedisUtil.convertValue(cachedData, ProductResponse.class);
            return cachedProduct;
        }

        ProductEntity existingProduct = productRepository
                .findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        ProductResponse productResponse = ProductResponse.fromProduct(existingProduct);

        setProductToRedis(cachedKey, productResponse);

        return productResponse;
    }

    private void setProductToRedis(String cachedKey, Object object) {
        baseRedisService.set(cachedKey, object);
        baseRedisService.setTimeToLive(cachedKey, 1);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductDto productDto) {
        ProductEntity existingProduct = getProductEntityById(id);
        CategoryEntity existingCategory = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        existingProduct.setName(productDto.getName());
        existingProduct.setPrice(productDto.getPrice());
        existingProduct.setDescription(productDto.getDescription());
        existingProduct.setThumbnail(productDto.getThumbnail());
        existingProduct.setCategory(existingCategory);

        productRepository.save(existingProduct);

        ProductResponse productResponse = ProductResponse.fromProduct(existingProduct);

        String cachedKey = PRODUCT_CACHE_PREFIX + id;
        baseRedisService.delete(cachedKey);
        setProductToRedis(cachedKey, productResponse);

        return productResponse;
    }

    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        String cachedKey = PRODUCT_CACHE_PREFIX + "page:" + pageable.getPageNumber()
                + ":size:" + pageable.getPageSize() + ":sort:" + pageable.getSort();

        List<ProductResponse> cachedProducts = (List<ProductResponse>) baseRedisService.get(cachedKey);
        if (Objects.nonNull(cachedProducts)) {
            long totalElements = productRepository.count();
            return new PageImpl<>(cachedProducts, pageable, totalElements);
        }

        Page<ProductResponse> productResponsePage = productRepository
                .findAll(pageable)
                .map(ProductResponse::fromProduct);

        setProductToRedis(cachedKey, productResponsePage.getContent());
        return productResponsePage;
    }

    @Override
    public void deleteProduct(Long id) {
        ProductEntity productEntity = getProductEntityById(id);
        productRepository.delete(productEntity);

        String cacheKey = PRODUCT_CACHE_PREFIX + id;
        baseRedisService.delete(cacheKey);

        String getCachedKey = PRODUCT_CACHE_PREFIX + "page:*";
        clearAllProductsCache(getCachedKey);
    }

    private void clearAllProductsCache(String pattern) {
        Set<String> keys = baseRedisService.getKeys(pattern);
        keys.forEach(key -> baseRedisService.delete(key));
    }

    @Override
    public ProductImageResponse createProductImage(ProductImageDto productImageDto) {
        ProductEntity existingProduct = getProductEntityById(productImageDto.getProductId());
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
    public Page<ProductResponse> findProductsByCriteria(Map<String, String> searchCriteria, Pageable pageable) {
        Specification<ProductEntity> specification = Specification.where(null);
        String valueId = searchCriteria.get("id");
        String valueName = searchCriteria.get("name");
        String valueDescription = searchCriteria.get("description");
        String valueMinPrice = searchCriteria.get("minPrice");
        String valueMaxPrice = searchCriteria.get("maxPrice");
        String valueCategoryId = searchCriteria.get("categoryId");

        if (StringUtils.hasLength(valueId)) {
            specification = specification.and(ProductSpecs.fieldEquals("id", valueId));
        }

        if (StringUtils.hasLength(valueName)) {
            specification = specification.and(ProductSpecs.fieldContains("name", valueName));
        }

        if (StringUtils.hasLength(valueDescription)) {
            specification = specification.and(ProductSpecs.fieldContains("description", valueDescription));
        }

        if (StringUtils.hasLength(valueMinPrice)) {
            specification = specification.and(ProductSpecs.priceGreaterThan(Float.valueOf(valueMinPrice)));
        }

        if (StringUtils.hasLength(valueMaxPrice)) {
            specification = specification.and(ProductSpecs.priceLowerThan(Float.valueOf(valueMaxPrice)));
        }

        if (StringUtils.hasLength(valueCategoryId)) {
            specification = specification.and(ProductSpecs.fieldEquals("category.id", Long.valueOf(valueCategoryId)));
        }

        Page<ProductEntity> productEntityPage = this.productRepository.findAll(specification, pageable);
        Page<ProductResponse> productResponsePage = productEntityPage.map(ProductResponse::fromProduct);
        return productResponsePage;
    }
}
