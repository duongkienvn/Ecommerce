package com.project.shopapp.converter;

import com.project.shopapp.entity.CommentEntity;
import com.project.shopapp.model.response.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentConverter {
    private final ModelMapper modelMapper;

    public CommentResponse convertToCommentResponse(CommentEntity commentEntity) {
        CommentResponse commentResponse = new CommentResponse();
        modelMapper.map(commentEntity, commentResponse);
        commentResponse.setCreated_at(commentEntity.getCreatedAt());
        commentResponse.setUpdated_at(commentEntity.getUpdatedAt());

        return commentResponse;
    }
}
