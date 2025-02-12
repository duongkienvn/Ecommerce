package com.project.shopapp.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.shopapp.entity.OrderDetailsEntity;
import com.project.shopapp.entity.OrderEntity;
import com.project.shopapp.entity.ProductEntity;
import com.project.shopapp.exception.AppException;
import com.project.shopapp.exception.ErrorCode;
import com.project.shopapp.model.dto.OrderDetailsDto;
import com.project.shopapp.model.response.OrderDetailsResponse;
import com.project.shopapp.model.response.OrderResponse;
import com.project.shopapp.repository.OrderDetailsRepository;
import com.project.shopapp.repository.OrderRepository;
import com.project.shopapp.repository.ProductRepository;
import com.project.shopapp.service.IBaseRedisService;
import com.project.shopapp.service.IOrderDetailsService;
import com.project.shopapp.service.IOrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailsService implements IOrderDetailsService {
    private final OrderDetailsRepository orderDetailsRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final IBaseRedisService baseRedisService;
    private static final String ORDER_DETAILS_CACHE_PREFIX = "orderDetails:";


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
//        String cacheKey = ORDER_DETAILS_CACHE_PREFIX + id;
//        OrderDetailsResponse cachedOrderDetails = (OrderDetailsResponse) baseRedisService.get(cacheKey);
//
//        if (cachedOrderDetails != null) {
//            return cachedOrderDetails;
//        }

        OrderDetailsEntity orderDetails = orderDetailsRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_DETAILS_NOT_FOUND));

//        baseRedisService.setTimeToLive(cacheKey, 1);
//        baseRedisService.set(cacheKey, orderDetails);

        return orderDetails;
    }

    @Override
    @Transactional
    public void deleteOrderDetails(Long id) {
        OrderDetailsEntity existingOrderDetails = getOrderDetailsById(id);
        orderDetailsRepository.delete(existingOrderDetails);
    }

    @Override
    public List<OrderDetailsEntity> getOrderDetailsByOrderId(Long orderId) {
        List<OrderDetailsEntity> orderDetailsEntities = orderDetailsRepository.findByOrderId(orderId);
        return orderDetailsEntities;
    }
}
