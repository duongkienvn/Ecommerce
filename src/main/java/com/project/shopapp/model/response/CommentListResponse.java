package com.project.shopapp.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@Builder
public class CommentListResponse {
    private List<CommentResponse> commentResponses;
    private int totalPages;
}
