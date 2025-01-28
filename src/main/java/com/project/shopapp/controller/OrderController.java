package com.project.shopapp.controller;

import com.project.shopapp.converter.OrderConverter;
import com.project.shopapp.entity.OrderEntity;
import com.project.shopapp.model.dto.OrderDto;
import com.project.shopapp.model.response.OrderResponse;
import com.project.shopapp.model.response.PageResponse;
import com.project.shopapp.service.IOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
        return ResponseEntity.ok(orderResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderConverter
                .convertToOrderResponse(orderService.getOrderById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable Long id, @Valid @RequestBody OrderDto orderDto) {
        return ResponseEntity.ok(orderService.updateOrder(orderDto, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok("Deleting Order successfully!");
    }

    @GetMapping("/user/{user_id}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUserId(@PathVariable("user_id") Long userId) {
        return ResponseEntity.ok(orderService.findOrderByUserId(userId));
    }

    @GetMapping
    public ResponseEntity<?> getAllOrders(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "5") int limit
    ) {
        PageRequest pageRequest
                = PageRequest.of(page, limit, Sort.by("fullName").ascending());
        Page<OrderResponse> orderPage = orderService.getAllOrders(pageRequest);
        List<OrderResponse> orderResponses = orderPage.getContent();
        int totalPages = orderPage.getTotalPages();

        return ResponseEntity.ok(PageResponse.builder()
                .data(orderResponses)
                .totalPages(totalPages)
                .build());
    }
}
