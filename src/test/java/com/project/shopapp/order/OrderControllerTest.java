package com.project.shopapp.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.shopapp.converter.OrderConverter;
import com.project.shopapp.exception.AppException;
import com.project.shopapp.exception.ErrorCode;
import com.project.shopapp.model.dto.OrderDto;
import com.project.shopapp.model.response.OrderResponse;
import com.project.shopapp.service.IOrderService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class OrderControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    IOrderService orderService;

    @MockitoBean
    OrderConverter orderConverter;

    @Value("${api.prefix}")
    String baseUrl;

    @Autowired
    ObjectMapper objectMapper;

    private OrderDto orderDto;
    private OrderResponse orderResponse;

    @BeforeEach
    void setUp() {
        orderDto = new OrderDto();
        orderDto.setUserId(1L);
        orderDto.setFullname("duongkien");
        orderDto.setPhoneNumber("12345");

        orderResponse = new OrderResponse();
        orderResponse.setUserId(orderDto.getUserId());
        orderResponse.setFullName(orderDto.getFullname());
        orderResponse.setPhoneNumber(orderDto.getPhoneNumber());
    }

    @Test
    void testCreateOrder_Success() throws Exception {
        String json = objectMapper.writeValueAsString(orderDto);

        when(orderService.createOrder(any(OrderDto.class))).thenReturn(orderResponse);

        mockMvc.perform(post(baseUrl + "/orders")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("Create order successfully!"))
                .andExpect(jsonPath("$.data.fullname").value("duongkien"))
                .andExpect(jsonPath("$.data.user_id").value(1))
                .andExpect(jsonPath("$.data.phone_number").value(12345));
    }

    @Test
    void testCreateOrder_NotFound() throws Exception {
        String json = objectMapper.writeValueAsString(orderDto);

        ErrorCode errorCode = ErrorCode.ORDER_NOT_FOUND;
        when(orderService.createOrder(any(OrderDto.class))).thenThrow(new AppException(errorCode));

        mockMvc.perform(post(baseUrl + "/orders")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value(errorCode.getMessage()))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testUpdateOrder_Success() throws Exception {
        orderDto.setUserId(2l);
        orderDto.setFullname("kien");
        String json = objectMapper.writeValueAsString(orderDto);

        orderResponse.setUserId(orderDto.getUserId());
        orderResponse.setFullName(orderDto.getFullname());

        when(orderService.updateOrder(any(OrderDto.class), eq(1l))).thenReturn(orderResponse);

        mockMvc.perform(put(baseUrl + "/orders/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Update order successfully!"))
                .andExpect(jsonPath("$.data.user_id").value(2))
                .andExpect(jsonPath("$.data.fullname").value("kien"));
    }

    @Test
    void testUpdateOrder_OrderNotFound() throws Exception {
        orderDto.setUserId(2l);
        orderDto.setFullname("kien");
        String json = objectMapper.writeValueAsString(orderDto);

        orderResponse.setUserId(orderDto.getUserId());
        orderResponse.setFullName(orderDto.getFullname());

        ErrorCode errorCode = ErrorCode.ORDER_NOT_FOUND;
        when(orderService.updateOrder(any(OrderDto.class), eq(1l)))
                .thenThrow(new AppException(errorCode));

        mockMvc.perform(put(baseUrl + "/orders/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value(errorCode.getMessage()))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testUpdateOrder_UserNotFound() throws Exception {
        orderDto.setUserId(2l);
        orderDto.setFullname("kien");
        String json = objectMapper.writeValueAsString(orderDto);

        orderResponse.setUserId(orderDto.getUserId());
        orderResponse.setFullName(orderDto.getFullname());

        ErrorCode errorCode = ErrorCode.USER_NOT_EXISTED;
        when(orderService.updateOrder(any(OrderDto.class), eq(1l)))
                .thenThrow(new AppException(errorCode));

        mockMvc.perform(put(baseUrl + "/orders/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value(errorCode.getMessage()))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testGetOrderById_NotFound() throws Exception {
        ErrorCode errorCode = ErrorCode.ORDER_NOT_FOUND;
        when(orderService.getOrderById(1l)).thenThrow(new AppException(errorCode));

        mockMvc.perform(get(baseUrl + "/orders/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value(errorCode.getMessage()))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testDeleteOrder_Success() throws Exception {
        doNothing().when(orderService).deleteOrder(1l);

        mockMvc.perform(delete(baseUrl + "/orders/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Deleting Order successfully!"));
    }

    @Test
    void testDeleteOrder_NotFound() throws Exception {
        ErrorCode errorCode = ErrorCode.ORDER_NOT_FOUND;
        doThrow(new AppException(errorCode)).when(orderService).deleteOrder(1l);

        mockMvc.perform(delete(baseUrl + "/orders/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value(errorCode.getMessage()))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testGetOrderByUserId() throws Exception {
        when(orderService.findOrderByUserId(1l)).thenReturn(List.of(orderResponse));

        mockMvc.perform(get(baseUrl + "/orders/users/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Get order successfully!"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void testGetAllOrders_Success() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<OrderResponse> orderResponsePage = new PageImpl<>(List.of(orderResponse), pageable, 1);

        when(orderService.getAllOrders(pageable)).thenReturn(orderResponsePage);

        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("page", "0");
        requestParams.add("size", "10");
        mockMvc.perform(get(baseUrl + "/orders")
                        .params(requestParams)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Get all orders successfully!"))
                .andExpect(jsonPath("$.data.content", Matchers.hasSize(1)));
    }
}
