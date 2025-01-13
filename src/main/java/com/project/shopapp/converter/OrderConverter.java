package com.project.shopapp.converter;

import com.project.shopapp.entity.OrderEntity;
import com.project.shopapp.model.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderConverter {
    private final ModelMapper mapper;

    public OrderEntity convertToOrder(OrderDto orderDto) {
        return null;
    }
}
