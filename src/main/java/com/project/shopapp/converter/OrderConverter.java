package com.project.shopapp.converter;

import com.project.shopapp.entity.OrderEntity;
import com.project.shopapp.model.dto.OrderDto;
import com.project.shopapp.model.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderConverter {
    private final ModelMapper mapper;

    public OrderResponse convertToOrderResponse(OrderEntity order) {
        OrderResponse orderResponse = new OrderResponse();
        mapper.map(order, orderResponse);
        orderResponse.setUserId(order.getUser().getId());

        return orderResponse;
    }
}
