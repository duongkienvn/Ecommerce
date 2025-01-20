package com.project.shopapp.service;

import com.project.shopapp.entity.CommentEntity;
import com.project.shopapp.model.dto.CommentDto;
import com.project.shopapp.model.request.CommentUpdateRequest;
import com.project.shopapp.model.response.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface ICommentService {
    CommentResponse addComment(CommentDto commentDto);
    void deleteComment(Long commentId);
    void deleteComments(List<Long> commentIdList);
    CommentResponse updateComment(Long id, CommentUpdateRequest updateRequest);
    Page<CommentResponse> getAllCommentsByProductId(Long productId, PageRequest pageRequest);
    Page<CommentResponse> getAllCommentsByUserId(Long userId, PageRequest pageRequest);
    CommentEntity getCommentById(Long id);
}
