package com.project.shopapp.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;
@Builder
@AllArgsConstructor
@Data
public class ProductListResponse {
    private List<ProductResponse> productResponseList;
    private int totalPages;
}
