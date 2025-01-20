package com.project.shopapp.service.impl;

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
import com.project.shopapp.service.ICommentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService {
    private final CommentRepository commentRepository;
    private final UserRepostiory userRepostiory;
    private final ProductRepository productRepository;
    private final CommentConverter commentConverter;
    private final AuthenticationFacade authenticationFacade;

    @Override
    public CommentResponse addComment(CommentDto commentDto) {
        UserEntity existingUser = userRepostiory.findById(commentDto.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        ProductEntity existingProduct = productRepository.findById(commentDto.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        CommentEntity commentEntity = CommentEntity.builder()
                .content(commentDto.getContent())
                .user(existingUser)
                .productEntity(existingProduct)
                .isDeleted(false)
                .build();
        commentRepository.save(commentEntity);

        return commentConverter.convertToCommentResponse(commentEntity);
    }

    private boolean isAuthority(CommentEntity existingComment) {
        String currentName = authenticationFacade.getCurrentName();
        List<String> roles = authenticationFacade.getCurrentRoles();

        return roles.contains("ROLE_ADMIN")
                || existingComment.getUser().getPhoneNumber().equals(currentName);
    }

    @Override
    public void deleteComment(Long commentId) {
        CommentEntity existingComment = getCommentById(commentId);

        if (isAuthority(existingComment)) {
            commentRepository.deleteById(commentId);
        } else {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    @Override
    @Transactional
    public void deleteComments(List<Long> commentIdList) {
        commentIdList.forEach(id -> getCommentById(id));
        commentRepository.deleteCommentEntitiesByIdIn(commentIdList);
    }

    @Override
    public CommentResponse updateComment(Long id, CommentUpdateRequest updateRequest) {
        CommentEntity existingComment = getCommentById(id);

        String currentName = authenticationFacade.getCurrentName();

        if (!currentName.equals(existingComment.getUser().getPhoneNumber())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        existingComment.setContent(updateRequest.getContent());
        commentRepository.save(existingComment);

        return commentConverter.convertToCommentResponse(existingComment);
    }

    @Override
    public Page<CommentResponse> getAllCommentsByProductId(Long productId, PageRequest pageRequest) {
        Page<CommentEntity> commentEntities = commentRepository.getAllByProductEntityId(productId, pageRequest);
        return commentEntities.map(comment -> commentConverter.convertToCommentResponse(comment));
    }

    @Override
    public Page<CommentResponse> getAllCommentsByUserId(Long userId, PageRequest pageRequest) {
        Page<CommentEntity> commentEntities = commentRepository.getAllByUserId(userId, pageRequest);
        return commentEntities.map(comment -> commentConverter.convertToCommentResponse(comment));
    }

    @Override
    public CommentEntity getCommentById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
    }
}
