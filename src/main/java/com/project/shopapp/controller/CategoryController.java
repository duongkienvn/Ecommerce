package com.project.shopapp.controller;

import com.project.shopapp.entity.CategoryEntity;
import com.project.shopapp.model.dto.CategoryDto;
import com.project.shopapp.model.response.ApiResponse;
import com.project.shopapp.model.response.PageResponse;
import com.project.shopapp.service.ICategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final ICategoryService categoryService;

    @PostMapping
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        categoryService.createCategory(categoryDto);
        return ResponseEntity.ok(new ApiResponse(HttpStatus.CREATED.value(), "Insert category successfully!"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(
            @PathVariable Long id, @Valid @RequestBody CategoryDto categoryDto) {
        categoryService.updateCategory(categoryDto, id);
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Update category successfully!"));
    }

    @GetMapping
    public ResponseEntity<?> getAllCategories(Pageable pageable) {
        Page<CategoryEntity> categoryPage = categoryService.getAllCategories(pageable);
        int totalPage = categoryPage.getTotalPages();
        List<CategoryEntity> categoryEntities = categoryPage.getContent();

        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Get all categories successfully!",
                PageResponse.builder()
                .totalPages(totalPage)
                .data(categoryEntities).build()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Delete category successfully!"));
    }
}
