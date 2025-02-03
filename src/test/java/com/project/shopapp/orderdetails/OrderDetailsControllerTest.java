package com.project.shopapp.orderdetails;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.shopapp.entity.OrderDetailsEntity;
import com.project.shopapp.entity.OrderEntity;
import com.project.shopapp.entity.ProductEntity;
import com.project.shopapp.exception.AppException;
import com.project.shopapp.exception.ErrorCode;
import com.project.shopapp.model.dto.OrderDetailsDto;
import com.project.shopapp.model.response.OrderDetailsResponse;
import com.project.shopapp.service.IOrderDetailsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class OrderDetailsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IOrderDetailsService orderDetailsService;

    @Value("${api.prefix}")
    String baseUrl;

    @Autowired
    ObjectMapper objectMapper;

    private OrderDetailsEntity orderDetailsEntity;
    private OrderDetailsDto orderDetailsDto;
    private OrderDetailsResponse orderDetailsResponse;


    @BeforeEach
    void setUp() {
        orderDetailsDto = new OrderDetailsDto();
        orderDetailsDto.setNumberOfProducts(5);
        orderDetailsDto.setColor("Red");
        orderDetailsDto.setProductId(1l);
        orderDetailsDto.setOrderId(1l);

        ProductEntity product = new ProductEntity();
        product.setId(orderDetailsDto.getProductId());
        OrderEntity order = new OrderEntity();
        order.setId(orderDetailsDto.getOrderId());

        orderDetailsEntity = new OrderDetailsEntity();
        orderDetailsEntity.setId(1L);
        orderDetailsEntity.setNumberOfProducts(orderDetailsDto.getNumberOfProducts());
        orderDetailsEntity.setColor(orderDetailsDto.getColor());
        orderDetailsEntity.setProduct(product);
        orderDetailsEntity.setOrder(order);

        orderDetailsResponse = OrderDetailsResponse.fromOrderDetails(orderDetailsEntity);
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void createOrderDetails_Success() throws Exception {
        String json = objectMapper.writeValueAsString(orderDetailsDto);

        when(orderDetailsService.createOrderDetails(any(OrderDetailsDto.class))).thenReturn(orderDetailsEntity);

        mockMvc.perform(post(baseUrl + "/order_details")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.number_of_products").value(orderDetailsEntity.getNumberOfProducts()))
                .andExpect(jsonPath("$.data.color").value(orderDetailsEntity.getColor()))
                .andExpect(jsonPath("$.data.product_id").value(orderDetailsEntity.getProduct().getId()))
                .andExpect(jsonPath("$.data.order_id").value(orderDetailsEntity.getOrder().getId()));
    }

    @Test
    void updateOrderDetails_Success() throws Exception {
        orderDetailsDto.setColor("Blue");
        orderDetailsDto.setNumberOfProducts(2);
        String json = objectMapper.writeValueAsString(orderDetailsDto);

        orderDetailsEntity.setColor(orderDetailsDto.getColor());
        orderDetailsEntity.setNumberOfProducts(orderDetailsDto.getNumberOfProducts());
        when(orderDetailsService.updateOrderDetails(eq(1L),
                any(OrderDetailsDto.class))).thenReturn(orderDetailsEntity);

        mockMvc.perform(put(baseUrl + "/order_details/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.data.color").value("Blue"))
                .andExpect(jsonPath("$.data.number_of_products").value(2));
    }

    @Test
    void updateOrderDetails_NotFound() throws Exception {
        orderDetailsDto.setColor("Blue");
        orderDetailsDto.setNumberOfProducts(2);
        String json = objectMapper.writeValueAsString(orderDetailsDto);

        ErrorCode errorCode = ErrorCode.ORDER_DETAILS_NOT_FOUND;
        when(orderDetailsService.updateOrderDetails(eq(1L),
                any(OrderDetailsDto.class)))
                .thenThrow(new AppException(errorCode));

        mockMvc.perform(put(baseUrl + "/order_details/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value(errorCode.getMessage()))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void getOrderDetailsById_Success() throws Exception {
        when(orderDetailsService.getOrderDetailsById(1L)).thenReturn(orderDetailsEntity);

        mockMvc.perform(get(baseUrl + "/order_details/1").accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.number_of_products").value(orderDetailsEntity.getNumberOfProducts()))
                .andExpect(jsonPath("$.data.color").value(orderDetailsEntity.getColor()))
                .andExpect(jsonPath("$.data.product_id").value(orderDetailsEntity.getProduct().getId()))
                .andExpect(jsonPath("$.data.order_id").value(orderDetailsEntity.getOrder().getId()));
    }

    @Test
    void getOrderDetailsById_NotFound() throws Exception {
        ErrorCode errorCode = ErrorCode.ORDER_DETAILS_NOT_FOUND;
        when(orderDetailsService.getOrderDetailsById(1L)).thenThrow(new AppException(errorCode));

        mockMvc.perform(get(baseUrl + "/order_details/1").accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value(errorCode.getMessage()))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void deleteOrderDetails_Success() throws Exception {
        doNothing().when(orderDetailsService).deleteOrderDetails(1L);

        mockMvc.perform(delete(baseUrl + "/order_details/1").accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Delete order details successfully!"));
    }

    @Test
    void deleteOrderDetails_NotFound() throws Exception {
        ErrorCode errorCode = ErrorCode.ORDER_DETAILS_NOT_FOUND;
        doThrow(new AppException(errorCode)).when(orderDetailsService).deleteOrderDetails(1L);

        mockMvc.perform(delete(baseUrl + "/order_details/1").accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value(errorCode.getMessage()))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void getByOrderId_Success() throws Exception {
        when(orderDetailsService.getOrderDetailsByOrderId(1L)).thenReturn(List.of(orderDetailsEntity));

        mockMvc.perform(get(baseUrl + "/order_details/orders/1").accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Find order details successfully!"))
                .andExpect(jsonPath("$.data").exists());
    }
}
