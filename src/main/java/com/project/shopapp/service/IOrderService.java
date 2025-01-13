package com.project.shopapp.service;

import com.project.shopapp.entity.OrderEntity;
import com.project.shopapp.model.dto.OrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IOrderService {
    OrderEntity createOrder(OrderDto orderDto);
    OrderEntity getOrderById(Long id);
    OrderEntity updateOrder(OrderDto orderDto, Long id);
    void deleteOrder(Long id);
    List<OrderEntity> findOrderByUserId(Long userId);
    Page<OrderEntity> getAllOrders(PageRequest pageRequest);
}
