package com.project.shopapp.service.impl;

import com.project.shopapp.entity.OrderEntity;
import com.project.shopapp.entity.UserEntity;
import com.project.shopapp.enums.OrderStatus;
import com.project.shopapp.exception.AppException;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.exception.ErrorCode;
import com.project.shopapp.model.dto.OrderDto;
import com.project.shopapp.repository.OrderRepository;
import com.project.shopapp.repository.UserRepostiory;
import com.project.shopapp.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;
    private final UserRepostiory userRepostiory;
    private final ModelMapper modelMapper;

    @Override
    public OrderEntity createOrder(OrderDto orderDto) {
        UserEntity user = userRepostiory.findById(orderDto.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        modelMapper.typeMap(OrderDto.class, OrderEntity.class)
                .addMappings(mapper -> mapper.skip(OrderEntity::setId));

        OrderEntity order = new OrderEntity();
        modelMapper.map(orderDto, order);
        order.setUser(user);
        order.setOrderDate(new Date());
        order.setStatus(OrderStatus.PENDING.getStatus());
        LocalDate shippingDate = orderDto.getShippingDate() == null ? LocalDate.now() : orderDto.getShippingDate();
        if (shippingDate.isBefore(LocalDate.now())) {
            throw new AppException(ErrorCode.INVALID_DATE);
        }
        order.setShippingDate(shippingDate);
        order.setActive(true);
        orderRepository.save(order);

        return order;
    }

    @Override
    public OrderEntity getOrderById(Long id) {
        OrderEntity order = orderRepository
                .findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        return order;
    }

    @Override
    public OrderEntity updateOrder(OrderDto orderDto, Long id) {
        OrderEntity existingOrder = getOrderById(id);
        UserEntity existingUser = userRepostiory.findById(orderDto.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        modelMapper.typeMap(OrderDto.class, OrderEntity.class)
                .addMappings(mapper -> mapper.skip(OrderEntity::setId));
        modelMapper.map(orderDto, existingOrder);
        LocalDate shippingDate = orderDto.getShippingDate() == null ? LocalDate.now() : orderDto.getShippingDate();
        if (shippingDate.isBefore(LocalDate.now())) {
            throw new AppException(ErrorCode.INVALID_DATE);
        }
        existingOrder.setShippingDate(shippingDate);
        existingOrder.setUser(existingUser);

        return existingOrder;
    }

    @Override
    public void deleteOrder(Long id) {
        OrderEntity order = getOrderById(id);
        order.setActive(false);
        orderRepository.save(order);
    }

    @Override
    public List<OrderEntity> findOrderByUserId(Long userId) {
        List<OrderEntity> orderEntities = orderRepository.findOrderEntitiesByUserId(userId);
        return orderEntities;
    }

    @Override
    public Page<OrderEntity> getAllOrders(PageRequest pageRequest) {
        return orderRepository.findAll(pageRequest);
    }
}
