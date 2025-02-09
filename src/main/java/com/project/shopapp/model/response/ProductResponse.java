package com.project.shopapp.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.entity.ProductEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse extends BaseResponse {
    String name;
    String description;
    Float price;
    String thumbnail;

    @JsonProperty("category_id")
    Long categoryId;

    public static ProductResponse fromProduct(ProductEntity productEntity) {
        ProductResponse productResponse = ProductResponse.builder()
                .name(productEntity.getName())
                .price(productEntity.getPrice())
                .description(productEntity.getDescription())
                .thumbnail(productEntity.getThumbnail())
                .categoryId(productEntity.getCategory().getId())
                .build();
        productResponse.setCreated_at(productEntity.getCreatedAt());
        productResponse.setUpdated_at(productEntity.getUpdatedAt());

        return productResponse;
    }
}
