package com.project.shopapp.orderdetails;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.shopapp.exception.ErrorCode;
import com.project.shopapp.model.dto.OrderDetailsDto;
import com.project.shopapp.model.dto.UserLoginDto;
import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration tests for Order Details API endpoints")
@Tag("Integration")
@ActiveProfiles(value = "dev")
public class OrderDetailsIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    String userToken;
    String adminToken;

    @Value("${api.prefix}")
    String baseUrl;

    @BeforeEach
    void setUp() throws Exception {
        ResultActions userResultActions = mockMvc
                .perform(post(baseUrl + "/users/login").contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper()
                                .writeValueAsString(
                                        new UserLoginDto("3848394455", "securepassword"))));
        MvcResult userMvcResult = userResultActions.andDo(print()).andReturn();
        String userContent = userMvcResult.getResponse().getContentAsString();
        JSONObject userJsonObject = new JSONObject(userContent);
        this.userToken = "Bearer " + userJsonObject.getString("data");

        ResultActions adminResultActions = mockMvc.perform(post(baseUrl + "/users/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                        new ObjectMapper().writeValueAsString(
                                new UserLoginDto("3094992384", "securepassword"))));
        MvcResult adminMvcResult = adminResultActions.andDo(print()).andReturn();
        String adminContent = adminMvcResult.getResponse().getContentAsString();
        JSONObject adminJsonObject = new JSONObject(adminContent);
        this.adminToken = "Bearer " + adminJsonObject.getString("data");
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testGetOrderDetailsById_Success() throws Exception {
        this.mockMvc.perform(get(baseUrl + "/order_details/7")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, this.userToken))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Find order details successfully!"))
                .andExpect(jsonPath("$.data.id").value(7L))
                .andExpect(jsonPath("$.data.number_of_products").value(5))
                .andExpect(jsonPath("$.data.color").value("Red"));
    }

    @Test
    void testGetOrderDetailsById_NotFound() throws Exception {
        mockMvc.perform(get(baseUrl + "/order_details/-1").accept(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, this.userToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value(ErrorCode.ORDER_DETAILS_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testCreateOrderDetails_Success() throws Exception {
        OrderDetailsDto orderDetailsDto = new OrderDetailsDto();
        orderDetailsDto.setNumberOfProducts(5);
        orderDetailsDto.setColor("Red");
        orderDetailsDto.setProductId(7l);
        orderDetailsDto.setOrderId(2l);

        String json = objectMapper.writeValueAsString(orderDetailsDto);

        mockMvc.perform(post(baseUrl + "/order_details").contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, this.userToken))
                .andExpect(jsonPath("code").value(201))
                .andExpect(jsonPath("message").value("Add order details successfully!"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.number_of_products").value(5))
                .andExpect(jsonPath("$.data.color").value("Red"))
                .andExpect(jsonPath("$.data.product_id").value(7))
                .andExpect(jsonPath("$.data.order_id").value(2));
    }

    void testCreateOrderDetails_Exception(OrderDetailsDto orderDetailsDto, ErrorCode errorCode) throws Exception {
        String json = objectMapper.writeValueAsString(orderDetailsDto);
        mockMvc.perform(post(baseUrl + "/order_details")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, this.userToken))
                .andDo(print())
                .andExpect(jsonPath("status").value("NOT_FOUND"))
                .andExpect(jsonPath("message").value(errorCode.getMessage()))
                .andExpect(jsonPath("timestamp").exists());
    }

    @Test
    void testCreateOrderDetails_ProductNotFound() throws Exception {
        OrderDetailsDto orderDetailsDto = new OrderDetailsDto();
        orderDetailsDto.setNumberOfProducts(5);
        orderDetailsDto.setColor("Red");
        orderDetailsDto.setProductId(Long.MAX_VALUE);
        orderDetailsDto.setOrderId(2l);
        testCreateOrderDetails_Exception(orderDetailsDto, ErrorCode.PRODUCT_NOT_FOUND);
    }

    @Test
    void testCreateOrderDetails_OrderNotFound() throws Exception {
        OrderDetailsDto orderDetailsDto = new OrderDetailsDto();
        orderDetailsDto.setNumberOfProducts(5);
        orderDetailsDto.setColor("Red");
        orderDetailsDto.setProductId(7l);
        orderDetailsDto.setOrderId(Long.MAX_VALUE);
        testCreateOrderDetails_Exception(orderDetailsDto, ErrorCode.ORDER_NOT_FOUND);
    }

    @Test
    void testUpdateOrderDetails_Success() throws Exception {
        OrderDetailsDto orderDetailsDto = new OrderDetailsDto();
        orderDetailsDto.setNumberOfProducts(5);
        orderDetailsDto.setColor("Blue");
        orderDetailsDto.setProductId(7l);
        orderDetailsDto.setOrderId(2l);
        String json = objectMapper.writeValueAsString(orderDetailsDto);

        mockMvc.perform(put(baseUrl + "/order_details/8")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, this.userToken))
                .andExpect(jsonPath("code").value(200))
                .andExpect(jsonPath("message").value("Update order details successfully!"))
                .andExpect(jsonPath("$.data.color").value("Blue"))
                .andExpect(jsonPath("$.data.number_of_products").value(5));
    }

    @Test
    void testUpdateOrderDetails_NotFound() throws Exception {
        OrderDetailsDto orderDetailsDto = new OrderDetailsDto();
        orderDetailsDto.setOrderId(39394934939L);
        orderDetailsDto.setNumberOfProducts(5);
        orderDetailsDto.setColor("Blue");
        orderDetailsDto.setProductId(7l);
        orderDetailsDto.setOrderId(2l);
        String json = objectMapper.writeValueAsString(orderDetailsDto);
        mockMvc.perform(put(baseUrl + "/order_details/39394934939").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, this.userToken))
                .andExpect(jsonPath("status").value("NOT_FOUND"))
                .andExpect(jsonPath("message").value(ErrorCode.ORDER_DETAILS_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("timestamp").exists());
    }

    void testUpdateOrderDetails_Exception(OrderDetailsDto orderDetailsDto, Long id, ErrorCode errorCode) throws Exception {
        String json = objectMapper.writeValueAsString(orderDetailsDto);
        mockMvc.perform(put(baseUrl + "/order_details/{id}", id).contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, this.userToken))
                .andExpect(jsonPath("status").value("NOT_FOUND"))
                .andExpect(jsonPath("message").value(errorCode.getMessage()))
                .andExpect(jsonPath("timestamp").exists());
    }

    @Test
    void testUpdateOrderDetails_ProductNotFound() throws Exception {
        OrderDetailsDto orderDetailsDto = new OrderDetailsDto();
        orderDetailsDto.setOrderId(10L);
        orderDetailsDto.setNumberOfProducts(5);
        orderDetailsDto.setColor("Blue");
        orderDetailsDto.setProductId(Long.MAX_VALUE);
        orderDetailsDto.setOrderId(2l);
        testUpdateOrderDetails_Exception(orderDetailsDto, 10L, ErrorCode.PRODUCT_NOT_FOUND);
    }

    @Test
    void testUpdateOrderDetails_OrderNotFound() throws Exception {
        OrderDetailsDto orderDetailsDto = new OrderDetailsDto();
        orderDetailsDto.setOrderId(10L);
        orderDetailsDto.setNumberOfProducts(5);
        orderDetailsDto.setColor("Blue");
        orderDetailsDto.setProductId(7L);
        orderDetailsDto.setOrderId(Long.MAX_VALUE);
        testUpdateOrderDetails_Exception(orderDetailsDto, 10L, ErrorCode.ORDER_NOT_FOUND);
    }

    @Test
    @Transactional
    void testDeleteOrderDetails_Success() throws Exception {
        mockMvc.perform(delete(baseUrl + "/order_details/14").accept(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, this.adminToken))
                .andExpect(jsonPath("code").value(200))
                .andExpect(jsonPath("message").value("Delete order details successfully!"));
    }

    @Test
    void testDeleteOrderDetails_NotFound() throws Exception {
        mockMvc.perform(delete(baseUrl + "/order_details/-1").accept(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, this.adminToken))
                .andExpect(jsonPath("status").value("NOT_FOUND"))
                .andExpect(jsonPath("message").value(ErrorCode.ORDER_DETAILS_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("timestamp").exists());
    }

    @Test
    void testGetOrderDetailsByOrderId_Success() throws Exception {
        mockMvc.perform(get(baseUrl + "/order_details/orders/5").accept(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, this.userToken))
                .andExpect(jsonPath("code").value(200))
                .andExpect(jsonPath("message").value("Find order details successfully!"))
                .andExpect(jsonPath("data").exists());
    }
}
