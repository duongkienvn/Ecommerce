package com.project.shopapp.service.impl;

import com.project.shopapp.entity.CategoryEntity;
import com.project.shopapp.entity.ProductEntity;
import com.project.shopapp.exception.AppException;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.exception.ErrorCode;
import com.project.shopapp.model.dto.CategoryDto;
import com.project.shopapp.repository.CategoryRepository;
import com.project.shopapp.service.ICategoryService;
import com.project.shopapp.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryEntity createCategory(CategoryDto categoryDto) {
        CategoryEntity newCategory = CategoryEntity
                .builder()
                .name(categoryDto.getName())
                .build();

        return categoryRepository.save(newCategory);
    }

    @Override
    public CategoryEntity updateCategory(CategoryDto categoryDto, Long id) {
        CategoryEntity existingCategory = getCategoryById(id);
        existingCategory.setName(categoryDto.getName());

        return categoryRepository.save(existingCategory);
    }

    @Override
    public Page<CategoryEntity> getAllCategories(PageRequest pageRequest) {
        return categoryRepository.findAll(pageRequest);
    }

    @Override
    public CategoryEntity getCategoryById(Long id) {
        return categoryRepository
                .findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    @Override
    public void deleteCategory(Long id) {
        CategoryEntity categoryEntity = getCategoryById(id);
        categoryRepository.delete(categoryEntity);
    }
}
