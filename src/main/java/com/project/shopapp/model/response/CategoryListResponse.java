package com.project.shopapp.model.response;

import com.project.shopapp.entity.CategoryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CategoryListResponse {
    private List<CategoryEntity> categoryEntityList;
    private int totalPages;
}
