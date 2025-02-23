package com.project.shopapp.category;

import com.project.shopapp.entity.CategoryEntity;
import com.project.shopapp.exception.AppException;
import com.project.shopapp.exception.ErrorCode;
import com.project.shopapp.model.dto.CategoryDto;
import com.project.shopapp.repository.CategoryRepository;
import com.project.shopapp.service.impl.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(value = "dev")
public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private CategoryEntity categoryEntity;
    private CategoryDto categoryDto;

    @BeforeEach
    void setUp() {
        categoryDto = new CategoryDto();
        categoryDto.setName("Electronics");

        categoryEntity = new CategoryEntity();
        categoryEntity.setId(1l);
        categoryEntity.setName(categoryDto.getName());
    }

    @Test
    void testCreateCategory_Success() {
        when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(categoryEntity);

        CategoryEntity result = categoryService.createCategory(categoryDto);

        assertNotNull(result);
        assertEquals("Electronics", result.getName());
        verify(categoryRepository, times(1)).save(any(CategoryEntity.class));
    }

    @Test
    void testUpdateCategory_Success() {
        categoryDto.setName("Updated Electronics");
        categoryEntity.setName(categoryDto.getName());

        when(categoryRepository.findById(1l)).thenReturn(Optional.of(categoryEntity));
        when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(categoryEntity);

        CategoryEntity result = categoryService.updateCategory(categoryDto, 1l);

        assertNotNull(result);
        assertEquals("Updated Electronics", result.getName());
        verify(categoryRepository, times(1)).findById(1l);
        verify(categoryRepository, times(1)).save(any(CategoryEntity.class));
    }

    @Test
    void testUpdateCategory_NotFound() {
        when(categoryRepository.findById(1l)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class,
                () -> categoryService.updateCategory(categoryDto, 1l));

        assertEquals(ErrorCode.CATEGORY_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testGetCategoryById_Success() {
        when(categoryRepository.findById(1l)).thenReturn(Optional.of(categoryEntity));

        CategoryEntity result = categoryService.getCategoryById(1l);

        assertNotNull(result);
        assertEquals(1l, result.getId());
        verify(categoryRepository, times(1)).findById(1l);
    }

    @Test
    void testGetCategoryById_NotFound() {
        when(categoryRepository.findById(1l)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> categoryService.getCategoryById(1l));

        assertEquals(ErrorCode.CATEGORY_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testGetAllCategories_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CategoryEntity> categoryEntityPage = new PageImpl<>(List.of(categoryEntity), pageable, 1);

        when(categoryRepository.findAll(pageable)).thenReturn(categoryEntityPage);

        Page<CategoryEntity> result = categoryService.getAllCategories(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(categoryRepository, times(1)).findAll(pageable);
    }

    @Test
    void testDeleteCategory_Success() {
        when(categoryRepository.findById(1l)).thenReturn(Optional.of(categoryEntity));
        doNothing().when(categoryRepository).delete(categoryEntity);

        categoryService.deleteCategory(1l);

        verify(categoryRepository, times(1)).delete(categoryEntity);
    }

    @Test
    void testDeleteCategory_NotFound() {
        when(categoryRepository.findById(1l)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> categoryService.deleteCategory(1l));

        assertEquals(ErrorCode.CATEGORY_NOT_FOUND, exception.getErrorCode());
    }
}
