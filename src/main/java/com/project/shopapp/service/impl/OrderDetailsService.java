package com.project.shopapp.service.impl;

import com.project.shopapp.entity.OrderDetailsEntity;
import com.project.shopapp.entity.OrderEntity;
import com.project.shopapp.entity.ProductEntity;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.model.dto.OrderDetailsDto;
import com.project.shopapp.repository.OrderDetailsRepository;
import com.project.shopapp.repository.OrderRepository;
import com.project.shopapp.repository.ProductRepository;
import com.project.shopapp.service.IOrderDetailsService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailsService implements IOrderDetailsService {
    private final OrderDetailsRepository orderDetailsRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ModelMapper mapper;

    @Override
    public OrderDetailsEntity createOrderDetails(OrderDetailsDto orderDetailsDto) {
        OrderEntity existingOrder = orderRepository.findById(orderDetailsDto.getOrderId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find order with id: " + orderDetailsDto.getOrderId()));

        ProductEntity existingProduct = productRepository.findById(orderDetailsDto.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find product with id: " + orderDetailsDto.getProductId()));

        OrderDetailsEntity newOrderDetails = OrderDetailsEntity
                .builder()
                .order(existingOrder)
                .product(existingProduct)
                .numberOfProducts(orderDetailsDto.getNumberOfProducts())
                .color(orderDetailsDto.getColor())
                .totalMoney(orderDetailsDto.getTotalMoney())
                .price(orderDetailsDto.getPrice())
                .build();

        return orderDetailsRepository.save(newOrderDetails);
    }

    @Override
    public OrderDetailsEntity updateOrderDetails(Long id, OrderDetailsDto orderDetailsDto) {
        OrderDetailsEntity existingOrderDetails = orderDetailsRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find order details with id: " + id));

        OrderEntity existingOrder = orderRepository.findById(orderDetailsDto.getOrderId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find order with id: " + orderDetailsDto.getOrderId()));

        ProductEntity existingProduct = productRepository.findById(orderDetailsDto.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find product with id: " + orderDetailsDto.getProductId()));

        existingOrderDetails.setPrice(orderDetailsDto.getPrice());
        existingOrderDetails.setTotalMoney(orderDetailsDto.getTotalMoney());
        existingOrderDetails.setColor(orderDetailsDto.getColor());
        existingOrderDetails.setNumberOfProducts(orderDetailsDto.getNumberOfProducts());
        existingOrderDetails.setProduct(existingProduct);
        existingOrderDetails.setOrder(existingOrder);

        return orderDetailsRepository.save(existingOrderDetails);
    }

    @Override
    public OrderDetailsEntity getOrderDetailsById(Long id) {
        return orderDetailsRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find order details with id: " + id));
    }

    @Override
    public void deleteOrderDetails(Long id) {
        OrderDetailsEntity existingOrderDetails = getOrderDetailsById(id);
        orderDetailsRepository.delete(existingOrderDetails);
    }

    @Override
    public List<OrderDetailsEntity> getOrderDetailsByOrderId(Long orderId) {
        return orderDetailsRepository.findByOrderId(orderId);
    }
}
