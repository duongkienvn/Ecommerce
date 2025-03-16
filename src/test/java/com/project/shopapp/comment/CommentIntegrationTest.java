//package com.project.shopapp.comment;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.project.shopapp.entity.UserEntity;
//import com.project.shopapp.exception.AppException;
//import com.project.shopapp.exception.ErrorCode;
//import com.project.shopapp.model.dto.CommentDto;
//import com.project.shopapp.model.dto.UserLoginDto;
//import com.project.shopapp.model.request.CommentUpdateRequest;
//import com.project.shopapp.model.response.CommentResponse;
//import com.project.shopapp.service.ICommentService;
//import jakarta.transaction.Transactional;
//import org.hamcrest.Matchers;
//import org.json.JSONObject;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Tag;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
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
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.when;
//import static org.springframework.http.HttpHeaders.AUTHORIZATION;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc(addFilters = true)
//@Tag("Integration")
//@DisplayName("Integration tests for Comment API endpoints")
//@ActiveProfiles(value = "dev")
//public class CommentIntegrationTest {
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Value("${api.prefix}")
//    private String baseUrl;
//
//    private CommentDto commentDto;
//    private CommentUpdateRequest request;
//
//    private String adminToken;
//    private String userToken;
//
//    @BeforeEach
//    void setUp() throws Exception {
//        commentDto = new CommentDto();
//        commentDto.setContent("This is a great product!");
//        commentDto.setUserId(2l);
//        commentDto.setProductId(11l);
//
//        request = new CommentUpdateRequest();
//
//        // user token
//        UserLoginDto user = new UserLoginDto("0987654322", "123456");
//        String jsonUser = objectMapper.writeValueAsString(user);
//        ResultActions userResult = mockMvc.perform(post(baseUrl + "/users/login")
//                .accept(MediaType.APPLICATION_JSON_VALUE)
//                .content(jsonUser)
//                .contentType(MediaType.APPLICATION_JSON_VALUE));
//        MvcResult userMvcResult = userResult.andDo(print()).andReturn();
//        String userContent = userMvcResult.getResponse().getContentAsString();
//        JSONObject userObject = new JSONObject(userContent);
//        this.userToken = "Bearer " + userObject.getString("data");
//
//        // admin token
//        UserLoginDto admin = new UserLoginDto("3094992384", "securepassword");
//        String jsonAdmin = objectMapper.writeValueAsString(admin);
//        ResultActions adminResult = mockMvc.perform(post(baseUrl + "/users/login")
//                .accept(MediaType.APPLICATION_JSON_VALUE)
//                .content(jsonAdmin)
//                .contentType(MediaType.APPLICATION_JSON_VALUE));
//        MvcResult adminMvcResult = adminResult.andDo(print()).andReturn();
//        String adminContent = adminMvcResult.getResponse().getContentAsString();
//        JSONObject adminObject = new JSONObject(adminContent);
//        this.adminToken = "Bearer " + adminObject.getString("data");
//    }
//
//    @Test
//    void testAddComment_Success() throws Exception {
//        String json = objectMapper.writeValueAsString(commentDto);
//
//        mockMvc.perform(post(baseUrl + "/comments")
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(json)
//                        .accept(MediaType.APPLICATION_JSON_VALUE)
//                        .header(AUTHORIZATION, this.userToken))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.code").value(201))
//                .andExpect(jsonPath("$.message").value("Add comment successfully!"))
//                .andExpect(jsonPath("$.data.content").value("This is a great product!"))
//                .andExpect(jsonPath("$.data.product_id").value(11l))
//                .andExpect(jsonPath("$.data.user_id").value(2l));
//    }
//
//    @Test
//    void testAddComment_UserNotFound() throws Exception {
//        commentDto.setUserId(12323232l);
//        String json = objectMapper.writeValueAsString(commentDto);
//
//        ErrorCode errorCode = ErrorCode.USER_NOT_EXISTED;
//
//        mockMvc.perform(post(baseUrl + "/comments")
//                        .content(json)
//                        .accept(MediaType.APPLICATION_JSON_VALUE)
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .header(AUTHORIZATION, this.userToken))
//                .andExpect(jsonPath("$.message").value(errorCode.getMessage()))
//                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
//                .andExpect(jsonPath("$.timestamp").exists());
//    }
//
//    @Test
//    void testAddComment_ProductNotFound() throws Exception {
//        commentDto.setProductId(1323233l);
//        String json = objectMapper.writeValueAsString(commentDto);
//
//        ErrorCode errorCode = ErrorCode.PRODUCT_NOT_FOUND;
//
//        mockMvc.perform(post(baseUrl + "/comments")
//                        .content(json)
//                        .accept(MediaType.APPLICATION_JSON_VALUE)
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .header(AUTHORIZATION, this.userToken))
//                .andExpect(jsonPath("$.message").value(errorCode.getMessage()))
//                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
//                .andExpect(jsonPath("$.timestamp").exists());
//    }
//
//    @Test
//    void testUpdateComment_Success() throws Exception {
//        request.setContent("That's good");
//        String json = objectMapper.writeValueAsString(request);
//
//
//        mockMvc.perform(put(baseUrl + "/comments/9")
//                        .content(json)
//                        .accept(MediaType.APPLICATION_JSON_VALUE)
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .header(AUTHORIZATION, this.userToken))
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.message").value("Update comment successfully!"))
//                .andExpect(jsonPath("$.data.content").value("That's good"));
//    }
//
//    @Test
//    void testUpdateComment_NotFound() throws Exception {
//        request.setContent("That's good");
//        String json = objectMapper.writeValueAsString(request);
//
//        ErrorCode errorCode = ErrorCode.COMMENT_NOT_FOUND;
//
//        mockMvc.perform(put(baseUrl + "/comments/43434343434")
//                        .content(json)
//                        .accept(MediaType.APPLICATION_JSON_VALUE)
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .header(AUTHORIZATION, this.userToken))
//                .andExpect(jsonPath("$.message").value(errorCode.getMessage()))
//                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
//                .andExpect(jsonPath("$.timestamp").exists());
//    }
//
//    @Test
//    @Transactional
//    void testDeleteComment_Success() throws Exception {
//        mockMvc.perform(delete(baseUrl + "/comments/16")
//                        .accept(MediaType.APPLICATION_JSON_VALUE)
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .header(AUTHORIZATION, this.adminToken))
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.message").value("Delete Comment successfully!"));
//    }
//
//    @Test
//    @Transactional
//    void testDeleteComments_Success() throws Exception {
//        List<Long> ids = List.of(16l, 17l);
//        String json = objectMapper.writeValueAsString(ids);
//
//        mockMvc.perform(delete(baseUrl + "/comments/bulk-delete")
//                        .content(json)
//                        .accept(MediaType.APPLICATION_JSON_VALUE)
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .header(AUTHORIZATION, this.adminToken))
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.message").value("Delete Comments successfully!"));
//    }
//
//    @Test
//    void getCommentsByUserId_Success() throws Exception {
//        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
//        requestParams.add("page", "0");
//        requestParams.add("size", "10");
//
//        mockMvc.perform(get(baseUrl + "/comments/users/2")
//                        .accept(MediaType.APPLICATION_JSON_VALUE)
//                        .params(requestParams)
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .header(AUTHORIZATION, this.adminToken))
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.message").value("Get all comments by user's id successfully!"))
//                .andExpect(jsonPath("$.data.content").exists());
//    }
//
//    @Test
//    void getCommentsByProductId_Success() throws Exception {
//        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
//        requestParams.add("page", "0");
//        requestParams.add("size", "10");
//
//        mockMvc.perform(get(baseUrl + "/comments/products/9")
//                        .accept(MediaType.APPLICATION_JSON_VALUE)
//                        .params(requestParams)
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .header(AUTHORIZATION, this.userToken))
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.message").value("Get all comments by product's id successfully!"))
//                .andExpect(jsonPath("$.data.content").exists());
//    }
//}
