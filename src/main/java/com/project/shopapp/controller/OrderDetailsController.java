package com.project.shopapp.controller;

import com.project.shopapp.entity.OrderDetailsEntity;
import com.project.shopapp.model.dto.OrderDetailsDto;
import com.project.shopapp.model.response.OrderDetailsResponse;
import com.project.shopapp.service.IOrderDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/order_details")
public class OrderDetailsController {
    private final IOrderDetailsService orderDetailsService;

    @PostMapping
    public ResponseEntity<?> creatOrderDetails(@Valid @RequestBody OrderDetailsDto orderDetailsDto, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errorMessages);
        }
        OrderDetailsEntity orderDetailsEntity = orderDetailsService.createOrderDetails(orderDetailsDto);
        return ResponseEntity.ok(OrderDetailsResponse.fromOrderDetails(orderDetailsEntity));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetailsById(@PathVariable Long id) {
        OrderDetailsEntity orderDetailsEntity = orderDetailsService.getOrderDetailsById(id);
        return ResponseEntity.ok(OrderDetailsResponse.fromOrderDetails(orderDetailsEntity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrderDetails(@PathVariable Long id, @Valid @RequestBody OrderDetailsDto orderDetailsDto, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errorMessages);
        }
        OrderDetailsEntity existingOrderDetails = orderDetailsService.updateOrderDetails(id, orderDetailsDto);
        System.out.println();
        return ResponseEntity.ok(OrderDetailsResponse.fromOrderDetails(existingOrderDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrderDetails(@PathVariable Long id) {
        orderDetailsService.deleteOrderDetails(id);
        return ResponseEntity.ok(String.format("Deleting order details with id %d successfully!", id));
    }

    @GetMapping("/order/{id}")
    public ResponseEntity<?> getOrderDetailsByOrderId(@PathVariable("id") Long orderId) {
        List<OrderDetailsEntity> orderDetailsEntities = orderDetailsService.getOrderDetailsByOrderId(orderId);
        return ResponseEntity.ok(orderDetailsEntities
                .stream()
                .map(OrderDetailsResponse::fromOrderDetails)
                .toList());
    }
}
