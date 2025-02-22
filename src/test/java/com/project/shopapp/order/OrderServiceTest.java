package com.project.shopapp.order;

import com.project.shopapp.converter.OrderConverter;
import com.project.shopapp.entity.OrderEntity;
import com.project.shopapp.entity.UserEntity;
import com.project.shopapp.enums.OrderStatus;
import com.project.shopapp.exception.AppException;
import com.project.shopapp.exception.ErrorCode;
import com.project.shopapp.model.dto.OrderDto;
import com.project.shopapp.model.response.OrderResponse;
import com.project.shopapp.repository.OrderRepository;
import com.project.shopapp.repository.UserRepostiory;
import com.project.shopapp.service.IOrderService;
import com.project.shopapp.service.impl.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(value = "dev")
public class OrderServiceTest {
    @Mock
    OrderRepository orderRepository;

    @Mock
    UserRepostiory userRepostiory;

    @Mock
    ModelMapper modelMapper;

    @Mock
    OrderConverter orderConverter;

    @InjectMocks
    OrderService orderService;

    private UserEntity user;
    private OrderDto orderDto;
    private OrderEntity order;
    private OrderResponse orderResponse;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId(1L);
        user.setFullName("duongkien");
        user.setActive(1);

        order = new OrderEntity();
        order.setId(2L);
        order.setUser(user);
        order.setOrderDate(new Date());
        order.setShippingDate(LocalDate.now().plusDays(1));
        order.setActive(true);
        order.setStatus(OrderStatus.PENDING.getStatus());

        orderDto = new OrderDto();
        orderDto.setUserId(1L);
        orderDto.setShippingDate(LocalDate.now().plusDays(1));

        orderResponse = new OrderResponse();
        orderResponse.setStatus(OrderStatus.PENDING.getStatus());
        orderResponse.setUserId(user.getId());
    }

//    @Test
//    void testCreateOrder_Success() {
//        // given
//        when(userRepostiory.findById(anyLong())).thenReturn(Optional.of(user));
////        when(modelMapper.map(OrderDto.class, OrderEntity.class)).thenAnswer(invocation -> {
////            OrderEntity newOrder = new OrderEntity();
////            newOrder.setUser(user);
////            newOrder.setOrderDate(new Date());
////            newOrder.setShippingDate(orderDto.getShippingDate());
////            newOrder.setStatus(OrderStatus.PENDING.getStatus());
////            newOrder.setActive(true);
////            return newOrder;
////        });
//        when(modelMapper.getTypeMap(OrderDto.class, OrderEntity.class)).thenReturn(null);
//        when(modelMapper.map(orderDto, OrderEntity.class)).thenReturn(order);
//        when(orderRepository.save(any(OrderEntity.class))).thenReturn(order);
//        when(orderConverter.convertToOrderResponse(any(OrderEntity.class))).thenReturn(orderResponse);
//
//        // when
//        OrderResponse result = orderService.createOrder(orderDto);
//
//        // then
//        assertNotNull(result);
//        assertEquals(orderResponse.getUserId(), result.getUserId());
//        assertEquals(orderResponse.getStatus(), result.getStatus());
//    }

    @Test
    void testCreateOrder_UserNotExists() {
        when(userRepostiory.findById(1L)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> orderService.createOrder(orderDto));

        assertEquals(exception.getErrorCode(), ErrorCode.USER_NOT_EXISTED);
        verify(userRepostiory, times(1)).findById(orderDto.getUserId());
        verify(orderRepository, never()).save(any(OrderEntity.class));
    }

    @Test
    void testUpdateOrder() {

    }

    @Test
    void testGetOrderById_Success() {
        // given
        when(orderRepository.findById(2L)).thenReturn(Optional.of(order));

        // when
        OrderEntity result = orderService.getOrderById(2L);

        // then
        assertNotNull(result);
        assertEquals(order.getId(), result.getId());
    }

    @Test
    void testGetOrderById_NotFound() {
        when(orderRepository.findById(2L)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> orderService.getOrderById(2L));

        assertEquals(exception.getErrorCode(), ErrorCode.ORDER_NOT_FOUND);
    }

    @Test
    void testFindOrderByUserId_Success() {
        when(orderRepository.findOrderEntitiesByUserId(1L)).thenReturn(List.of(order));
        when(orderConverter.convertToOrderResponse(any(OrderEntity.class))).thenReturn(orderResponse);

        List<OrderResponse> result = orderService.findOrderByUserId(1L);

        assertEquals(1, result.size());
        assertEquals(result.get(0).getOrderDate(), orderResponse.getOrderDate());
        assertEquals(result.get(0).getUserId(), orderResponse.getUserId());
    }

    @Test
    void testDeleteOrder_Success() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(order);

        orderService.deleteOrder(2L);

        verify(orderRepository, times(1)).save(any(OrderEntity.class));
        assertFalse(order.getActive());
    }

    @Test
    void testDeleteOrder_NotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> orderService.deleteOrder(2L));

        assertEquals(exception.getErrorCode(), ErrorCode.ORDER_NOT_FOUND);
    }

    @Test
    void testGetAllOrders() {
        Pageable pageable = PageRequest.of(0, 20);
        List<OrderEntity> orderEntities = List.of(order);
        Page<OrderEntity> orderEntityPage = new PageImpl<>(orderEntities);
        when(orderRepository.findAll(pageable)).thenReturn(orderEntityPage);
        when(orderConverter.convertToOrderResponse(any(OrderEntity.class))).thenReturn(orderResponse);

        Page<OrderResponse> orderResponses = orderService.getAllOrders(pageable);

        assertNotNull(orderResponses);
        assertEquals(1, orderResponses.getSize());
        assertEquals(orderResponses.getContent().get(0).getUserId(), orderResponse.getUserId());
    }
}
