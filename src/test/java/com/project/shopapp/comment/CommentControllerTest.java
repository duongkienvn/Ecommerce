package com.project.shopapp.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.shopapp.exception.AppException;
import com.project.shopapp.exception.ErrorCode;
import com.project.shopapp.model.dto.CommentDto;
import com.project.shopapp.model.request.CommentUpdateRequest;
import com.project.shopapp.model.response.CommentResponse;
import com.project.shopapp.service.ICommentService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles(value = "dev")
@AutoConfigureMockMvc(addFilters = false)
public class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ICommentService commentService;

    @Value("${api.prefix}")
    String baseUrl;

    @Autowired
    ObjectMapper objectMapper;

    private CommentDto commentDto;
    private CommentResponse commentResponse;
    private CommentUpdateRequest request;

    @BeforeEach
    void setUP() {
        commentDto = new CommentDto();
        commentDto.setContent("That's great!");
        commentDto.setProductId(1l);
        commentDto.setUserId(1l);

        request = new CommentUpdateRequest();

        commentResponse = new CommentResponse();
        commentResponse.setProductId(commentDto.getProductId());
        commentResponse.setUserId(commentDto.getUserId());
        commentResponse.setContent(commentDto.getContent());
    }

    @Test
    void testAddComment_Success() throws Exception {
        String json = objectMapper.writeValueAsString(commentDto);

        when(commentService.addComment(any(CommentDto.class))).thenReturn(commentResponse);

        mockMvc.perform(post(baseUrl + "/comments")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("Add comment successfully!"))
                .andExpect(jsonPath("$.data.content").value("That's great!"))
                .andExpect(jsonPath("$.data.product_id").value(1l))
                .andExpect(jsonPath("$.data.user_id").value(1l));
    }

    @Test
    void testAddComment_UserNotFound() throws Exception {
        String json = objectMapper.writeValueAsString(commentDto);

        ErrorCode errorCode = ErrorCode.USER_NOT_EXISTED;
        when(commentService.addComment(any(CommentDto.class))).thenThrow(new AppException(errorCode));

        mockMvc.perform(post(baseUrl + "/comments")
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message").value(errorCode.getMessage()))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testAddComment_ProductNotFound() throws Exception {
        String json = objectMapper.writeValueAsString(commentDto);

        ErrorCode errorCode = ErrorCode.PRODUCT_NOT_FOUND;
        when(commentService.addComment(any(CommentDto.class))).thenThrow(new AppException(errorCode));

        mockMvc.perform(post(baseUrl + "/comments")
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message").value(errorCode.getMessage()))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testUpdateComment_Success() throws Exception {
        request.setContent("That's good");
        commentResponse.setContent(request.getContent());
        String json = objectMapper.writeValueAsString(request);

        when(commentService.updateComment(eq(1l), any(CommentUpdateRequest.class))).thenReturn(commentResponse);

        mockMvc.perform(put(baseUrl + "/comments/1")
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Update comment successfully!"))
                .andExpect(jsonPath("$.data.content").value("That's good"));
    }

    @Test
    void testUpdateComment_NotFound() throws Exception {
        request.setContent("That's good");
        String json = objectMapper.writeValueAsString(request);

        ErrorCode errorCode = ErrorCode.COMMENT_NOT_FOUND;
        when(commentService.updateComment(eq(1l), any(CommentUpdateRequest.class))).thenThrow(new AppException(errorCode));

        mockMvc.perform(put(baseUrl + "/comments/1")
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message").value(errorCode.getMessage()))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testDeleteComment_Success() throws Exception {
        doNothing().when(commentService).deleteComment(anyLong());

        mockMvc.perform(delete(baseUrl + "/comments/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Delete Comment successfully!"));
    }

    @Test
    void testDeleteComment_NotFound() throws Exception {
        ErrorCode errorCode = ErrorCode.COMMENT_NOT_FOUND;
        doThrow(new AppException(errorCode)).when(commentService).deleteComment(anyLong());

        mockMvc.perform(delete(baseUrl + "/comments/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message").value(errorCode.getMessage()))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testDeleteComments_Success() throws Exception {
        List<Long> ids = List.of(1l);
        String json = objectMapper.writeValueAsString(ids);
        doNothing().when(commentService).deleteComments(ids);

        mockMvc.perform(delete(baseUrl + "/comments/bulk-delete")
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Delete Comments successfully!"));
    }

    @Test
    void getCommentsByUserId_Success() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CommentResponse> commentResponsePage = new PageImpl<>(List.of(commentResponse), pageable, 1);
        when(commentService.getAllCommentsByUserId(1l, pageable)).thenReturn(commentResponsePage);

        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("page", "0");
        requestParams.add("size", "10");

        mockMvc.perform(get(baseUrl + "/comments/users/1")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .params(requestParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Get all comments by user's id successfully!"))
                .andExpect(jsonPath("$.data.content", Matchers.hasSize(1)));
    }

    @Test
    void getCommentsByProductId_Success() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CommentResponse> commentResponsePage = new PageImpl<>(List.of(commentResponse), pageable, 1);
        when(commentService.getAllCommentsByProductId(1l, pageable)).thenReturn(commentResponsePage);

        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("page", "0");
        requestParams.add("size", "10");

        mockMvc.perform(get(baseUrl + "/comments/products/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .params(requestParams)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Get all comments by product's id successfully!"))
                .andExpect(jsonPath("$.data.content", Matchers.hasSize(1)));
    }
}
