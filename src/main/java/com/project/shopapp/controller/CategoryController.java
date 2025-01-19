package com.project.shopapp.controller;

import com.project.shopapp.entity.CategoryEntity;
import com.project.shopapp.model.dto.CategoryDto;
import com.project.shopapp.model.response.CategoryListResponse;
import com.project.shopapp.service.ICategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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
        return ResponseEntity.ok("Insert category successfully!");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(
            @PathVariable Long id, @Valid @RequestBody CategoryDto categoryDto) {
        categoryService.updateCategory(categoryDto, id);
        return ResponseEntity.ok("Update category successfully!");
    }

    @GetMapping
    public ResponseEntity<CategoryListResponse> getAllCategories(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "5") int limit
    ) {
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("id"));
        Page<CategoryEntity> categoryPage = categoryService.getAllCategories(pageRequest);
        int totalPage = categoryPage.getTotalPages();
        List<CategoryEntity> categoryEntities = categoryPage.getContent();

        return ResponseEntity.ok(CategoryListResponse.builder()
                .categoryEntityList(categoryEntities)
                .totalPages(totalPage)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Delete category successfully!");
    }
}
