package com.project.shopapp.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageResponse<T> {
    private int totalPages;
    private T data;
}
