package com.project.shopapp.service;

import com.project.shopapp.model.response.VNPayResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface IPaymentService {
    VNPayResponse createVnPayPayment(HttpServletRequest request);
}
