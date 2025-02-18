package com.project.shopapp.specification;

import com.project.shopapp.entity.ProductEntity;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecs {
    public static Specification<ProductEntity> fieldEquals(String fieldName, Object value) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(fieldName), value);
    }

    public static Specification<ProductEntity> fieldContains(String fieldName, String value) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder
                        .like(criteriaBuilder.lower(root.get(fieldName)), "%" + value.toLowerCase() + "%");
    }

    public static Specification<ProductEntity> priceGreaterThan(Float minPrice) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<ProductEntity> priceLowerThan(Float maxPrice) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
    }
}
