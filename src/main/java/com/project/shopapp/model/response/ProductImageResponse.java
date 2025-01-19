package com.project.shopapp.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class ProductImageResponse {
    @JsonProperty("image_url")
    String imageUrl;

    @JsonProperty("product_id")
    Long productId;
}
