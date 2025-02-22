package com.project.shopapp.controller;

import com.project.shopapp.converter.OrderConverter;
import com.project.shopapp.entity.OrderEntity;
import com.project.shopapp.model.dto.OrderDto;
import com.project.shopapp.model.response.ApiResponse;
import com.project.shopapp.model.response.OrderResponse;
import com.project.shopapp.model.response.PageResponse;
import com.project.shopapp.service.IOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;
    private final OrderConverter orderConverter;

    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderDto orderDto) {
        OrderResponse orderResponse = orderService.createOrder(orderDto);
        return ResponseEntity.ok(
                new ApiResponse(
                        HttpStatus.CREATED.value(), "Create order successfully!", orderResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        OrderEntity order = orderService.getOrderById(id);
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Get order successfully!",
                orderConverter.convertToOrderResponse(order)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable Long id, @Valid @RequestBody OrderDto orderDto) {
        return ResponseEntity.ok(
                new ApiResponse(HttpStatus.OK.value(), "Update order successfully!",
                orderService.updateOrder(orderDto, id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Deleting Order successfully!"));
    }

    @GetMapping("/users/{user_id}")
    public ResponseEntity<?> getOrdersByUserId(@PathVariable("user_id") Long userId) {
        return ResponseEntity.ok(
                new ApiResponse(HttpStatus.OK.value(), "Get order successfully!",
                        orderService.findOrderByUserId(userId)));
    }

    @GetMapping
    public ResponseEntity<?> getAllOrders(Pageable pageable) {
        Page<OrderResponse> orderPage = orderService.getAllOrders(pageable);
        List<OrderResponse> orderResponses = orderPage.getContent();
        int totalPages = orderPage.getTotalPages();

        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Get all orders successfully!",
                PageResponse.builder()
                .content(orderResponses)
                .totalPages(totalPages)
                .build()));
    }
}
