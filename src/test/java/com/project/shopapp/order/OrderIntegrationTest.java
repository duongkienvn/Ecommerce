//package com.project.shopapp.order;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.project.shopapp.exception.ErrorCode;
//import com.project.shopapp.model.dto.OrderDto;
//import com.project.shopapp.model.dto.UserLoginDto;
//import org.hamcrest.Matchers;
//import org.json.JSONObject;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Tag;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//
//import java.time.LocalDate;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@DisplayName("Integration tests for Order API endpoints")
//@Tag("Integration")
//@ActiveProfiles(value = "dev")
//public class OrderIntegrationTest {
//    @Autowired
//    MockMvc mockMvc;
//
//    @Autowired
//    ObjectMapper objectMapper;
//
//    @Value("${api.prefix}")
//    String baseUrl;
//
//    OrderDto orderDto;
//    String adminToken;
//    String userToken;
//
//    @BeforeEach
//    void setUp() throws Exception {
//        orderDto = new OrderDto();
//        orderDto.setUserId(2L);
//        orderDto.setFullName("John Doe");
//        orderDto.setEmail("johndoe@example.com");
//        orderDto.setPhoneNumber("1234567890");
//        orderDto.setAddress("123 Main Street, Some City");
//        orderDto.setNote("Please deliver by tomorrow.");
//        orderDto.setTotalMoney(150.75);
//        orderDto.setShippingMethod("Standard Shipping");
//        orderDto.setShippingAddress("123 Main Street, Some City");
//        orderDto.setShippingDate(LocalDate.of(2026, 2, 20));
//        orderDto.setPaymentMethod("Credit Card");
//
//        UserLoginDto user = new UserLoginDto("0987654322", "123456");
//        String userJson = objectMapper.writeValueAsString(user);
//        ResultActions userResultActions = mockMvc.perform(post(baseUrl + "/users/login")
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .content(userJson)
//                .accept(MediaType.APPLICATION_JSON_VALUE));
//        MvcResult userResult = userResultActions.andDo(print()).andReturn();
//        String userContent = userResult.getResponse().getContentAsString();
//        JSONObject userObject = new JSONObject(userContent);
//        this.userToken = "Bearer " + userObject.getString("data");
//
//        UserLoginDto admin = new UserLoginDto("3094992384", "securepassword");
//        String adminJson = objectMapper.writeValueAsString(admin);
//        ResultActions adminResultActions = mockMvc.perform(post(baseUrl + "/users/login")
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .content(adminJson)
//                .accept(MediaType.APPLICATION_JSON_VALUE));
//        MvcResult adminResult = adminResultActions.andDo(print()).andReturn();
//        String adminContent = adminResult.getResponse().getContentAsString();
//        JSONObject adminObject = new JSONObject(adminContent);
//        this.adminToken = "Bearer " + adminObject.getString("data");
//    }
//
//    @Test
//    void testCreateOrder_Success() throws Exception {
//        String json = objectMapper.writeValueAsString(orderDto);
//
//        mockMvc.perform(post(baseUrl + "/orders")
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(json)
//                        .accept(MediaType.APPLICATION_JSON_VALUE)
//                        .header(HttpHeaders.AUTHORIZATION, this.userToken))
//                .andExpect(jsonPath("$.code").value(201))
//                .andExpect(jsonPath("$.message").value("Create order successfully!"))
//                .andExpect(jsonPath("$.data.fullname").value("John Doe"))
//                .andExpect(jsonPath("$.data.user_id").value(2))
//                .andExpect(jsonPath("$.data.phone_number").value(1234567890));
//    }
//
//    @Test
//    void testCreateOrder_UserNotFound() throws Exception {
//        orderDto.setUserId(Long.MAX_VALUE);
//        String json = objectMapper.writeValueAsString(orderDto);
//        ErrorCode errorCode = ErrorCode.USER_NOT_EXISTED;
//
//        mockMvc.perform(post(baseUrl + "/orders")
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(json)
//                        .accept(MediaType.APPLICATION_JSON_VALUE)
//                        .header(HttpHeaders.AUTHORIZATION, this.userToken))
//                .andDo(print())
//                .andExpect(jsonPath("status").value("NOT_FOUND"))
//                .andExpect(jsonPath("message").value(errorCode.getMessage()))
//                .andExpect(jsonPath("timestamp").exists());
//    }
//
//    @Test
//    void testUpdateOrder_Success() throws Exception {
//        orderDto.setFullName("Alex");
//        orderDto.setEmail("alex@gmail.com");
//        orderDto.setPhoneNumber("343939394");
//
//        String json = objectMapper.writeValueAsString(orderDto);
//
//        mockMvc.perform(put(baseUrl + "/orders/1")
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(json)
//                        .accept(MediaType.APPLICATION_JSON_VALUE)
//                        .header(HttpHeaders.AUTHORIZATION, this.userToken))
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.message").value("Update order successfully!"))
//                .andExpect(jsonPath("$.data.email").value("alex@gmail.com"))
//                .andExpect(jsonPath("$.data.phone_number").value("343939394"))
//                .andExpect(jsonPath("$.data.fullname").value("Alex"));
//    }
//
//    @Test
//    void testUpdateOrder_OrderNotFound() throws Exception {
//        orderDto.setFullName("Alex");
//        orderDto.setEmail("alex@gmail.com");
//        orderDto.setPhoneNumber("343939394");
//
//        String json = objectMapper.writeValueAsString(orderDto);
//
//        ErrorCode errorCode = ErrorCode.ORDER_NOT_FOUND;
//        mockMvc.perform(put(baseUrl + "/orders/493498389498349")
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(json)
//                        .accept(MediaType.APPLICATION_JSON_VALUE)
//                        .header(HttpHeaders.AUTHORIZATION, this.userToken))
//                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
//                .andExpect(jsonPath("$.message").value(errorCode.getMessage()))
//                .andExpect(jsonPath("$.timestamp").exists());
//    }
//
//    @Test
//    void testUpdateOrder_UserNotFound() throws Exception {
//        orderDto.setUserId(Long.MAX_VALUE);
//        String json = objectMapper.writeValueAsString(orderDto);
//
//        ErrorCode errorCode = ErrorCode.USER_NOT_EXISTED;
//        mockMvc.perform(put(baseUrl + "/orders/1")
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(json).accept(MediaType.APPLICATION_JSON_VALUE)
//                        .header(HttpHeaders.AUTHORIZATION, this.userToken))
//                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
//                .andExpect(jsonPath("$.message").value(errorCode.getMessage()))
//                .andExpect(jsonPath("$.timestamp").exists());
//    }
//
//    @Test
//    void testDeleteOrder_Success() throws Exception {
//        mockMvc.perform(delete(baseUrl + "/orders/17")
//                        .accept(MediaType.APPLICATION_JSON_VALUE)
//                        .header(HttpHeaders.AUTHORIZATION, this.adminToken))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.message").value("Deleting Order successfully!"));
//    }
//
//    @Test
//    void testDeleteOrder_NotFound() throws Exception {
//        ErrorCode errorCode = ErrorCode.ORDER_NOT_FOUND;
//
//        mockMvc.perform(delete(baseUrl + "/orders/1788438483434")
//                        .accept(MediaType.APPLICATION_JSON_VALUE)
//                        .header(HttpHeaders.AUTHORIZATION, this.adminToken))
//                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
//                .andExpect(jsonPath("$.message").value(errorCode.getMessage()))
//                .andExpect(jsonPath("$.timestamp").exists());
//    }
//
//    @Test
//    void testGetOrderByUserId() throws Exception {
//        mockMvc.perform(get(baseUrl + "/orders/users/1")
//                        .accept(MediaType.APPLICATION_JSON_VALUE)
//                        .header(HttpHeaders.AUTHORIZATION, this.adminToken))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.message").value("Get order successfully!"))
//                .andExpect(jsonPath("$.data").exists());
//    }
//
//    @Test
//    void testGetAllOrders_Success() throws Exception {
//        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
//        requestParams.add("page", "0");
//        requestParams.add("size", "10");
//        mockMvc.perform(get(baseUrl + "/orders")
//                        .params(requestParams)
//                        .accept(MediaType.APPLICATION_JSON_VALUE)
//                        .header(HttpHeaders.AUTHORIZATION, this.userToken))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.message").value("Get all orders successfully!"))
//                .andExpect(jsonPath("$.data.content", Matchers.hasSize(10)));
//    }
//}
