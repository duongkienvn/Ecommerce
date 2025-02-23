//package com.project.shopapp.category;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.project.shopapp.entity.CategoryEntity;
//import com.project.shopapp.exception.AppException;
//import com.project.shopapp.exception.ErrorCode;
//import com.project.shopapp.model.dto.CategoryDto;
//import com.project.shopapp.model.dto.UserLoginDto;
//import com.project.shopapp.service.ICategoryService;
//import jakarta.transaction.Transactional;
//import org.hamcrest.Matchers;
//import org.json.JSONObject;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Tag;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.test.annotation.DirtiesContext;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@DisplayName("Integration tests for Category API endpoints")
//@Tag("Integration")
//@ActiveProfiles(value = "dev")
//public class CategoryIntegrationTest {
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Value("${api.prefix}")
//    private String baseUrl;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private String userToken;
//    private String adminToken;
//
//    private CategoryDto categoryDto;
//
//    @BeforeEach
//    void setUp() throws Exception {
//        categoryDto = new CategoryDto();
//        categoryDto.setName("Electronics");
//
//        UserLoginDto user = new UserLoginDto("0987654322", "123456");
//        String userJson = objectMapper.writeValueAsString(user);
//        ResultActions userResultActions = mockMvc.perform(post(baseUrl + "/users/login")
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .content(userJson)
//                .accept(MediaType.APPLICATION_JSON_VALUE));
//        MvcResult userResult = userResultActions.andDo(print()).andReturn();
//        String userContent = userResult.getResponse().getContentAsString();
//        JSONObject userObject = new JSONObject(userContent);
//        this.userToken = "Bearer " + userObject.getString("data");
//
//        UserLoginDto admin = new UserLoginDto("3094992384", "securepassword");
//        String adminJson = objectMapper.writeValueAsString(admin);
//        ResultActions adminResultActions = mockMvc.perform(post(baseUrl + "/users/login")
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .content(adminJson)
//                .accept(MediaType.APPLICATION_JSON_VALUE));
//        MvcResult adminResult = adminResultActions.andDo(print()).andReturn();
//        String adminContent = adminResult.getResponse().getContentAsString();
//        JSONObject adminObject = new JSONObject(adminContent);
//        this.adminToken = "Bearer " + adminObject.getString("data");
//    }
//
//    @Test
//    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
//    void testCreateCategory_Success() throws Exception {
//        String json = objectMapper.writeValueAsString(categoryDto);
//
//        mockMvc.perform(post(baseUrl + "/categories")
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(json)
//                        .accept(MediaType.APPLICATION_JSON_VALUE)
//                        .header(HttpHeaders.AUTHORIZATION, this.adminToken))
//                .andExpect(jsonPath("$.code").value(201))
//                .andExpect(jsonPath("$.message").value("Insert category successfully!"))
//                .andExpect(jsonPath("$.data.name").value("Electronics"));
//    }
//
//    @Test
//    void testUpdateCategory_Success() throws Exception {
//        categoryDto.setName("Updated Electronics");
//
//        String json = objectMapper.writeValueAsString(categoryDto);
//        mockMvc.perform(put(baseUrl + "/categories/2")
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(json)
//                        .accept(MediaType.APPLICATION_JSON_VALUE)
//                        .header(HttpHeaders.AUTHORIZATION, this.adminToken))
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.message").value("Update category successfully!"));
//    }
//
//    @Test
//    void testUpdateCategory_NotFound() throws Exception {
//        categoryDto.setName("Updated Electronics");
//
//        String json = objectMapper.writeValueAsString(categoryDto);
//        ErrorCode errorCode = ErrorCode.CATEGORY_NOT_FOUND;
//
//        mockMvc.perform(put(baseUrl + "/categories/8438483848384")
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(json)
//                        .accept(MediaType.APPLICATION_JSON_VALUE)
//                        .header(HttpHeaders.AUTHORIZATION, this.adminToken))
//                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
//                .andExpect(jsonPath("$.message").value(errorCode.getMessage()))
//                .andExpect(jsonPath("$.timestamp").exists());
//    }
//
//    @Test
//    @Transactional
//    void testDeleteCategory_Success() throws Exception {
//        mockMvc.perform(delete(baseUrl + "/categories/9")
//                        .accept(MediaType.APPLICATION_JSON_VALUE)
//                        .header(HttpHeaders.AUTHORIZATION, this.adminToken))
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.message").value("Delete category successfully!"));
//    }
//
//    @Test
//    void testDeleteCategory_NotFound() throws Exception {
//        ErrorCode errorCode = ErrorCode.CATEGORY_NOT_FOUND;
//
//        mockMvc.perform(delete(baseUrl + "/categories/888438483443")
//                        .accept(MediaType.APPLICATION_JSON_VALUE)
//                        .header(HttpHeaders.AUTHORIZATION, this.adminToken))
//                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
//                .andExpect(jsonPath("$.message").value(errorCode.getMessage()))
//                .andExpect(jsonPath("$.timestamp").exists());
//    }
//
//    @Test
//    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
//    void testGetAllCategories_Success() throws Exception {
//        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
//        requestParams.add("page", "0");
//        requestParams.add("size", "10");
//
//        mockMvc.perform(get(baseUrl + "/categories")
//                        .accept(MediaType.APPLICATION_JSON_VALUE)
//                        .params(requestParams)
//                        .header(HttpHeaders.AUTHORIZATION, this.adminToken))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.message").value("Get all categories successfully!"));
//    }
//}
