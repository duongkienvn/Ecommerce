package com.project.shopapp.service;

import com.project.shopapp.entity.OrderDetailsEntity;
import com.project.shopapp.model.dto.OrderDetailsDto;

import java.util.List;

public interface IOrderDetailsService {
    OrderDetailsEntity createOrderDetails(OrderDetailsDto orderDetailsDto);
    OrderDetailsEntity updateOrderDetails(Long id, OrderDetailsDto orderDetailsDto);
    OrderDetailsEntity getOrderDetailsById(Long id);
    void deleteOrderDetails(Long id);
    List<OrderDetailsEntity> getOrderDetailsByOrderId(Long orderId);
}
