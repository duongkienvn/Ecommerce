package com.project.shopapp.service.impl;

import com.project.shopapp.entity.OrderDetailsEntity;
import com.project.shopapp.entity.OrderEntity;
import com.project.shopapp.entity.ProductEntity;
import com.project.shopapp.exception.AppException;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.exception.ErrorCode;
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
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        ProductEntity existingProduct = productRepository.findById(orderDetailsDto.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

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
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_DETAILS_NOT_FOUND));

        OrderEntity existingOrder = orderRepository.findById(orderDetailsDto.getOrderId())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        ProductEntity existingProduct = productRepository.findById(orderDetailsDto.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

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
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_DETAILS_NOT_FOUND));
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
