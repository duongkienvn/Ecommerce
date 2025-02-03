package com.project.shopapp.controller;

import com.project.shopapp.entity.OrderDetailsEntity;
import com.project.shopapp.model.dto.OrderDetailsDto;
import com.project.shopapp.model.response.ApiResponse;
import com.project.shopapp.model.response.OrderDetailsResponse;
import com.project.shopapp.service.IOrderDetailsService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.eclipse.angus.mail.iap.Response.OK;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/order_details")
public class OrderDetailsController {
    private final IOrderDetailsService orderDetailsService;

    @PostMapping
    public ResponseEntity<?> creatOrderDetails(@Valid @RequestBody OrderDetailsDto orderDetailsDto) {
        OrderDetailsEntity orderDetailsEntity = orderDetailsService.createOrderDetails(orderDetailsDto);
        OrderDetailsResponse orderDetailsResponse = OrderDetailsResponse.fromOrderDetails(orderDetailsEntity);
        return ResponseEntity.status(CREATED)
                .body(new ApiResponse(CREATED.value(), "Add order details successfully!", orderDetailsResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetailsById(@PathVariable Long id) {
        OrderDetailsEntity orderDetailsEntity = orderDetailsService.getOrderDetailsById(id);
        OrderDetailsResponse orderDetailsResponse = OrderDetailsResponse.fromOrderDetails(orderDetailsEntity);
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Find order details successfully!", orderDetailsResponse));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrderDetails(@PathVariable Long id, @Valid @RequestBody OrderDetailsDto orderDetailsDto) {
        OrderDetailsEntity existingOrderDetails = orderDetailsService.updateOrderDetails(id, orderDetailsDto);
        OrderDetailsResponse orderDetailsResponse = OrderDetailsResponse.fromOrderDetails(existingOrderDetails);
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Update order details successfully!", orderDetailsResponse));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrderDetails(@PathVariable Long id) {
        orderDetailsService.deleteOrderDetails(id);
        return ResponseEntity.ok(new ApiResponse(
                HttpStatus.OK.value(), "Delete order details successfully!"));
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<?> getOrderDetailsByOrderId(@PathVariable("id") Long orderId) {
        List<OrderDetailsEntity> orderDetailsEntities = orderDetailsService.getOrderDetailsByOrderId(orderId);
        List<OrderDetailsResponse> orderDetailsResponses = orderDetailsEntities
                .stream()
                .map(OrderDetailsResponse::fromOrderDetails)
                .toList();

        return ResponseEntity.ok(new ApiResponse(
                HttpStatus.OK.value(), "Find order details successfully!", orderDetailsResponses));
    }
}
