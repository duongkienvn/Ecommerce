package com.project.shopapp.controller;

import com.project.shopapp.entity.OrderEntity;
import com.project.shopapp.model.dto.OrderDto;
import com.project.shopapp.model.response.OrderListResponse;
import com.project.shopapp.service.IOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderDto orderDto) {
        OrderEntity newOrder = orderService.createOrder(orderDto);
        return ResponseEntity.ok(newOrder);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable Long id, @Valid @RequestBody OrderDto orderDto) {
        OrderEntity order = orderService.updateOrder(orderDto, id);
        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok("Deleting Order successfully!");
    }

    @GetMapping("/user/{user_id}")
    public ResponseEntity<List<OrderEntity>> getOrdersByUserId(@PathVariable("user_id") Long userId) {
        return ResponseEntity.ok(orderService.findOrderByUserId(userId));
    }

    @GetMapping
    public ResponseEntity<OrderListResponse> getAllOrders(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ) {
        PageRequest pageRequest
                = PageRequest.of(page, limit, Sort.by("fullName").ascending());
        Page<OrderEntity> orderPage = orderService.getAllOrders(pageRequest);
        List<OrderEntity> orderEntities = orderPage.getContent();
        int totalPages = orderPage.getTotalPages();

        return ResponseEntity.ok(OrderListResponse.builder()
                .orderEntityList(orderEntities)
                .totalPages(totalPages)
                .build());
    }
}
