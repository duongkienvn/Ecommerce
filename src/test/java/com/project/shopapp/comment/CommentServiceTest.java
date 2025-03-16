package com.project.shopapp.comment;

import com.project.shopapp.authentication.AuthenticationFacade;
import com.project.shopapp.converter.CommentConverter;
import com.project.shopapp.entity.CommentEntity;
import com.project.shopapp.entity.ProductEntity;
import com.project.shopapp.entity.UserEntity;
import com.project.shopapp.exception.AppException;
import com.project.shopapp.exception.ErrorCode;
import com.project.shopapp.model.dto.CommentDto;
import com.project.shopapp.model.request.CommentUpdateRequest;
import com.project.shopapp.model.response.CommentResponse;
import com.project.shopapp.repository.CommentRepository;
import com.project.shopapp.repository.ProductRepository;
import com.project.shopapp.repository.UserRepostiory;
import com.project.shopapp.service.impl.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(value = "dev")
public class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepostiory userRepostiory;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CommentConverter commentConverter;

    @Mock
    private AuthenticationFacade authenticationFacade;

    @InjectMocks
    private CommentService commentService;

    private CommentDto commentDto;
    private CommentEntity commentEntity;
    private CommentResponse commentResponse;
    private UserEntity user;
    private ProductEntity product;
    private CommentUpdateRequest request;

    @BeforeEach
    void setUp() {
        request = new CommentUpdateRequest();

        commentDto = new CommentDto();
        commentDto.setContent("That's great!");
        commentDto.setProductId(1l);
        commentDto.setUserId(1l);

        user = new UserEntity();
        user.setId(commentDto.getUserId());

        product = new ProductEntity();
        product.setId(commentDto.getProductId());

        commentEntity = new CommentEntity();
        commentEntity.setId(1l);
        commentEntity.setContent(commentDto.getContent());
        commentEntity.setUser(user);
        commentEntity.setProductEntity(product);
        commentEntity.setIsDeleted(false);

        commentResponse =  new CommentResponse();
        commentResponse.setProductId(commentEntity.getProductEntity().getId());
        commentResponse.setUserId(commentEntity.getUser().getId());
        commentResponse.setContent(commentEntity.getContent());
    }

    @Test
    void testAddComment_Success() {
        when(userRepostiory.findById(anyLong())).thenReturn(Optional.of(user));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(commentRepository.save(any(CommentEntity.class))).thenReturn(commentEntity);
        when(commentConverter.convertToCommentResponse(any(CommentEntity.class))).thenReturn(commentResponse);

        CommentResponse result = commentService.addComment(commentDto);

        assertNotNull(result);
        assertEquals("That's great!", result.getContent());
        assertEquals(1l, result.getUserId());
        assertEquals(1l, result.getProductId());
        verify(commentRepository, times(1)).save(any(CommentEntity.class));
    }

    @Test
    void testAddComment_UserNotFound() {
        when(userRepostiory.findById(anyLong())).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> commentService.addComment(commentDto));

        assertNotNull(exception);
        assertEquals(ErrorCode.USER_NOT_EXISTED, exception.getErrorCode());
    }

    @Test
    void testAddComment_ProductNotFound() {
        when(userRepostiory.findById(anyLong())).thenReturn(Optional.of(user));
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> commentService.addComment(commentDto));

        assertNotNull(exception);
        assertEquals(ErrorCode.PRODUCT_NOT_FOUND, exception.getErrorCode());
    }

//    @Test
//    void testUpdateComment_Success() {
//        // TODO
//        request.setContent("That's good!");
//        commentEntity.setContent(commentDto.getContent());
//        commentResponse.setContent(commentEntity.getContent());
//
//        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(commentEntity));
//        when(commentRepository.save(any(CommentEntity.class))).thenReturn(commentEntity);
//        when(commentConverter.convertToCommentResponse(any(CommentEntity.class))).thenReturn(commentResponse);
//
//        CommentResponse result = commentService.updateComment(1l, request);
//        assertNotNull(result);
//        assertEquals("That's good!", result.getContent());
//        verify(commentRepository, times(1)).save(any(CommentEntity.class));
//    }

    @Test
    void testGetCommentById_Success() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(commentEntity));

        CommentEntity result = commentService.getCommentById(1l);

        assertNotNull(result);
        assertEquals(1l, result.getId());
        verify(commentRepository, times(1)).findById(anyLong());
    }

    @Test
    void testGetCommentById_NotFound() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> commentService.getCommentById(1l));

        assertNotNull(exception);
        assertEquals(ErrorCode.COMMENT_NOT_FOUND, exception.getErrorCode());
        verify(commentRepository, times(1)).findById(anyLong());
    }

//    @Test
//    void testDeleteComment_Success() {
//        when(commentRepository.findById(1l)).thenReturn(Optional.of(commentEntity));
//        doNothing().when(commentRepository).delete(commentEntity);
//
//        commentService.deleteComment(1l);
//
//        verify(commentRepository, times(1)).deleteById(anyLong());
//    }
}
