package com.project.shopapp.service.impl;

import com.project.shopapp.config.VNPayConfig;
import com.project.shopapp.model.response.VNPayResponse;
import com.project.shopapp.service.IPaymentService;
import com.project.shopapp.utils.vnpay.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService implements IPaymentService {
    private final VNPayConfig vnPayConfig;

    @Override
    public VNPayResponse createVnPayPayment(HttpServletRequest request) {
        long amount = Integer.parseInt(request.getParameter("amount")) * 100L;
        String bankCode = request.getParameter("bankCode");

        Map<String, String> vnpParamMap = vnPayConfig.getVNPayConfig();
        vnpParamMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamMap.put("vnp_BankCode", bankCode);
        }
        vnpParamMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));

        String queryUrl = VNPayUtil.getPaymentUrl(vnpParamMap, true);
        String hashData = VNPayUtil.getPaymentUrl(vnpParamMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        log.info(hashData);

        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;

        return VNPayResponse.builder()
                .paymentUrl(paymentUrl)
                .build();
    }
}
