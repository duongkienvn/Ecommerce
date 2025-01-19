package com.project.shopapp.service;

import com.project.shopapp.entity.OrderEntity;
import com.project.shopapp.model.dto.OrderDto;
import com.project.shopapp.model.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IOrderService {
    OrderResponse createOrder(OrderDto orderDto);
    OrderEntity getOrderById(Long id);
    OrderResponse updateOrder(OrderDto orderDto, Long id);
    void deleteOrder(Long id);
    List<OrderResponse> findOrderByUserId(Long userId);
    Page<OrderResponse> getAllOrders(PageRequest pageRequest);
}
