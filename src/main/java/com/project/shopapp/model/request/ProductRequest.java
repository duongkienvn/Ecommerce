package com.project.shopapp.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.entity.CategoryEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRequest {
    String name;
    Float priceFrom;
    Float priceTo;
    String categoryName;
}
