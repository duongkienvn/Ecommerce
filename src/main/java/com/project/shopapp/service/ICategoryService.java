package com.project.shopapp.service;

import com.project.shopapp.entity.CategoryEntity;
import com.project.shopapp.model.dto.CategoryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICategoryService {
    CategoryEntity createCategory(CategoryDto categoryDto);
    CategoryEntity updateCategory(CategoryDto categoryDto, Long id);
    Page<CategoryEntity> getAllCategories(Pageable pageable);
    CategoryEntity getCategoryById(Long id);
    void deleteCategory(Long id);
}
