package com.project.shopapp.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.shopapp.entity.CategoryEntity;
import com.project.shopapp.exception.AppException;
import com.project.shopapp.exception.ErrorCode;
import com.project.shopapp.model.dto.CategoryDto;
import com.project.shopapp.service.ICategoryService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("dev")
public class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ICategoryService categoryService;

    @Value("${api.prefix}")
    private String baseUrl;

    @Autowired
    private ObjectMapper objectMapper;

    private CategoryDto categoryDto;
    private CategoryEntity categoryEntity;

    @BeforeEach
    void setUp() {
        categoryDto = new CategoryDto();
        categoryDto.setName("Electronics");

        categoryEntity = new CategoryEntity();
        categoryEntity.setId(1l);
        categoryEntity.setName(categoryDto.getName());
    }

    @Test
    void testCreateCategory_Success() throws Exception {
        String json = objectMapper.writeValueAsString(categoryDto);
        when(categoryService.createCategory(any(CategoryDto.class))).thenReturn(categoryEntity);

        mockMvc.perform(post(baseUrl + "/categories")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("Insert category successfully!"))
                .andExpect(jsonPath("$.data.name").value(categoryEntity.getName()));
    }

    @Test
    void testUpdateCategory_Success() throws Exception {
        categoryDto.setName("Updated Electronics");
        categoryEntity.setName(categoryDto.getName());

        String json = objectMapper.writeValueAsString(categoryDto);
        when(categoryService.updateCategory(any(CategoryDto.class), eq(1l))).thenReturn(categoryEntity);

        mockMvc.perform(put(baseUrl + "/categories/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Update category successfully!"));
    }

    @Test
    void testUpdateCategory_NotFound() throws Exception {
        categoryDto.setName("Updated Electronics");
        categoryEntity.setName(categoryDto.getName());

        String json = objectMapper.writeValueAsString(categoryDto);
        ErrorCode errorCode = ErrorCode.CATEGORY_NOT_FOUND;
        when(categoryService.updateCategory(any(CategoryDto.class), eq(2l)))
                .thenThrow(new AppException(errorCode));

        mockMvc.perform(put(baseUrl + "/categories/2")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value(errorCode.getMessage()))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testDeleteCategory_Success() throws Exception {
        doNothing().when(categoryService).deleteCategory(1l);

        mockMvc.perform(delete(baseUrl + "/categories/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Delete category successfully!"));
    }

    @Test
    void testDeleteCategory_NotFound() throws Exception {
        ErrorCode errorCode = ErrorCode.CATEGORY_NOT_FOUND;
        doThrow(new AppException(errorCode)).when(categoryService).deleteCategory(2l);

        mockMvc.perform(delete(baseUrl + "/categories/2")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value(errorCode.getMessage()))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testGetAllCategories_Success() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CategoryEntity> categoryEntityPage = new PageImpl<>(List.of(categoryEntity), pageable, 1);

        when(categoryService.getAllCategories(pageable)).thenReturn(categoryEntityPage);

        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("page", "0");
        requestParams.add("size", "10");

        mockMvc.perform(get(baseUrl + "/categories")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .params(requestParams))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Get all categories successfully!"))
                .andExpect(jsonPath("$.data.content", Matchers.hasSize(1)));
    }
}
