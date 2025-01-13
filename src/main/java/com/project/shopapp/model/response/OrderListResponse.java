package com.project.shopapp.model.response;

import com.project.shopapp.entity.OrderEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OrderListResponse {
    private List<OrderEntity> orderEntityList;
    private int totalPages;
}
