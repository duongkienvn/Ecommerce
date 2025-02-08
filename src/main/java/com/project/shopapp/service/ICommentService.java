package com.project.shopapp.service;

import com.project.shopapp.entity.CommentEntity;
import com.project.shopapp.model.dto.CommentDto;
import com.project.shopapp.model.request.CommentUpdateRequest;
import com.project.shopapp.model.response.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICommentService {
    CommentResponse addComment(CommentDto commentDto);
    void deleteComment(Long commentId);
    void deleteComments(List<Long> commentIdList);
    CommentResponse updateComment(Long id, CommentUpdateRequest updateRequest);
    Page<CommentResponse> getAllCommentsByProductId(Long productId, Pageable pageable);
    Page<CommentResponse> getAllCommentsByUserId(Long userId, Pageable pageable);
    CommentEntity getCommentById(Long id);
}
