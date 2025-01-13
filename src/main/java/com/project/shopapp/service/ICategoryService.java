package com.project.shopapp.service;

import com.project.shopapp.entity.CategoryEntity;
import com.project.shopapp.model.dto.CategoryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.awt.print.Pageable;
import java.util.List;

public interface ICategoryService {
    CategoryEntity createCategory(CategoryDto categoryDto);
    CategoryEntity updateCategory(CategoryDto categoryDto, Long id);
    Page<CategoryEntity> getAllCategories(PageRequest pageRequest);
    CategoryEntity getCategoryById(Long id);
    void deleteCategory(Long id);
}
