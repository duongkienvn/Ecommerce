package com.project.shopapp.controller;

import com.project.shopapp.model.response.ApiResponse;
import com.project.shopapp.service.IPaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/payment")
public class PaymentController {
    private final IPaymentService paymentService;

    @GetMapping("/vn-pay")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> pay(HttpServletRequest request) {
        return ResponseEntity.ok(
                new ApiResponse(HttpStatus.OK.value(),
                        "Success",
                        paymentService.createVnPayPayment(request)));
    }

    @GetMapping("/vn-pay-callback")
    public ResponseEntity<?> payCallbackHandler(HttpServletRequest request) {
        String status = request.getHeader("vnp_ResponseCode");
        if (status.equals("00")) {
            return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Success"));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Failed!"));
        }
    }
}
