package com.project.shopapp.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@AllArgsConstructor
@Builder
@Getter
@Setter
public class UserListResponse {
    private List<UserResponse> userResponseList;
    private int totalPages;
}
