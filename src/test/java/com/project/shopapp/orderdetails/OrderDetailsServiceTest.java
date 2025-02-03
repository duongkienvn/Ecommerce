package com.project.shopapp.orderdetails;

import com.project.shopapp.entity.OrderDetailsEntity;
import com.project.shopapp.entity.OrderEntity;
import com.project.shopapp.entity.ProductEntity;
import com.project.shopapp.exception.AppException;
import com.project.shopapp.exception.ErrorCode;
import com.project.shopapp.model.dto.OrderDetailsDto;
import com.project.shopapp.repository.OrderDetailsRepository;
import com.project.shopapp.repository.OrderRepository;
import com.project.shopapp.repository.ProductRepository;
import com.project.shopapp.service.impl.OrderDetailsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderDetailsServiceTest {
    @Mock
    OrderDetailsRepository orderDetailsRepository;

    @Mock
    OrderRepository orderRepository;

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    OrderDetailsService orderDetailsService;

    private OrderDetailsEntity orderDetails;
    private OrderEntity order;
    private ProductEntity product;
    private OrderDetailsDto orderDetailsDto;

    @BeforeEach
    void setUp() {
        order = new OrderEntity();
        order.setId(1L);

        product = new ProductEntity();
        product.setId(1L);

        orderDetails = new OrderDetailsEntity();
        orderDetails.setId(1L);
        orderDetails.setOrder(order);
        orderDetails.setProduct(product);
        orderDetails.setNumberOfProducts(2);
        orderDetails.setColor("Red");
        orderDetails.setPrice(100F);
        orderDetails.setTotalMoney(200F);

        orderDetailsDto = new OrderDetailsDto(
                1L, 1L, 100F, 2 , 200F, "Red");
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void testGetOrderDetailsById_Success() {
        when(orderDetailsRepository.findById(1L)).thenReturn(Optional.of(orderDetails));

        OrderDetailsEntity foundedOrderDetailed = orderDetailsService.getOrderDetailsById(1L);

        assertNotNull(foundedOrderDetailed);
        assertEquals(1L, foundedOrderDetailed.getId());
    }

    @Test
    void testGetOrderDetailsById_NotFound() {
        when(orderDetailsRepository.findById(1L)).thenReturn(Optional.empty());

        AppException appException = assertThrows(AppException.class,
                () -> orderDetailsService.getOrderDetailsById(1L));

        assertEquals(ErrorCode.ORDER_DETAILS_NOT_FOUND.getMessage(), appException.getMessage());
    }

    @Test
    void testGetOrderDetailsByOrderId_Success() {
        when(orderDetailsRepository.findByOrderId(1L)).thenReturn(List.of(orderDetails));

        List<OrderDetailsEntity> orderDetailsEntities = orderDetailsService.getOrderDetailsByOrderId(1L);

        assertFalse(orderDetailsEntities.isEmpty());
        assertEquals(1, orderDetailsEntities.size());
    }

    @Test
    void deleteOrderDetails_Success() {
        when(orderDetailsRepository.findById(1L)).thenReturn(Optional.of(orderDetails));
        doNothing().when(orderDetailsRepository).delete(any(OrderDetailsEntity.class));

        assertDoesNotThrow(() -> orderDetailsService.deleteOrderDetails(1L));

        verify(orderDetailsRepository, times(1)).delete(any(OrderDetailsEntity.class));
    }

    @Test
    void deleteOrderDetails_NotFound() {
        when(orderDetailsRepository.findById(1L)).thenReturn(Optional.empty());

        AppException appException = assertThrows(AppException.class,
                () -> orderDetailsService.deleteOrderDetails(1L));

        assertEquals(ErrorCode.ORDER_DETAILS_NOT_FOUND.getMessage(), appException.getMessage());
        verify(orderDetailsRepository, times(0)).delete(any(OrderDetailsEntity.class));
    }

    @Test
    void createOrderDetails_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderDetailsRepository.save(any(OrderDetailsEntity.class))).thenReturn(orderDetails);

        OrderDetailsEntity createdOrderDetails = orderDetailsService.createOrderDetails(orderDetailsDto);

        assertNotNull(createdOrderDetails);
        assertEquals(2, createdOrderDetails.getNumberOfProducts());
        assertEquals("Red", createdOrderDetails.getColor());
        assertEquals(200F, createdOrderDetails.getTotalMoney());
    }

    @Test
    void createOrderDetails_OrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> orderDetailsService.createOrderDetails(orderDetailsDto));

        assertEquals(ErrorCode.ORDER_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    void createOrderDetails_ProductNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> orderDetailsService.createOrderDetails(orderDetailsDto));

        assertEquals(ErrorCode.PRODUCT_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    void updateOrderDetails_Success() {
        orderDetailsDto.setColor("Blue");
        orderDetailsDto.setTotalMoney(500F);
        orderDetailsDto.setNumberOfProducts(5);

        when(orderDetailsRepository.findById(1L)).thenReturn(Optional.of(orderDetails));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderDetailsRepository.save(any(OrderDetailsEntity.class))).thenReturn(orderDetails);

        OrderDetailsEntity updatedOrderDetails = orderDetailsService.updateOrderDetails(1L, orderDetailsDto);

        assertNotNull(updatedOrderDetails);
        assertEquals("Blue", updatedOrderDetails.getColor());
        assertEquals(500F, updatedOrderDetails.getTotalMoney());
        assertEquals(5, updatedOrderDetails.getNumberOfProducts());
        verify(orderDetailsRepository, times(1)).save(orderDetails);
    }

    @Test
    void updateOrderDetails_OrderDetailsNotFound() {
        when(orderDetailsRepository.findById(1L)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> orderDetailsService.updateOrderDetails(1L, orderDetailsDto));

        assertEquals(ErrorCode.ORDER_DETAILS_NOT_FOUND, exception.getErrorCode());
    }
}
