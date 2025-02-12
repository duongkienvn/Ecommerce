package com.project.shopapp.utils.vnpay;

import com.project.shopapp.exception.AppException;
import com.project.shopapp.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class VNPayUtil {
    public static String hmacSHA512(String key, String data) {
        try {
            if (key == null || data == null) {
                throw new AppException(ErrorCode.ILLEGAL_ARGUMENT);
            }

            Mac hmac512 = Mac.getInstance("HmacSHA512");

            byte[] hmacKeyBytes = key.getBytes();
            SecretKeySpec secretKeySpec = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKeySpec);

            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);

            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new AppException(ErrorCode.HMAC_EXCEPTION);
        }
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress = null;
        String forwardedIps = request.getHeader("X-FORWARDED-FOR");

        if (forwardedIps != null && !forwardedIps.isEmpty() && !"unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = forwardedIps.split(",")[0].trim();
        }

        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }

        return ipAddress;
    }

    public static String getPaymentUrl(Map<String, String> paramMap, boolean encodedKey) {
        return paramMap.entrySet().stream()
                .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> (encodedKey ? URLEncoder.encode(entry.getKey(), StandardCharsets.US_ASCII)
                        : entry.getKey()) + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII))
                .collect(Collectors.joining("&"));
    }

    public static String getRandomNumber(int len) {
        Random random = new Random();
        StringBuilder result = new StringBuilder(len);

        for (int i = 0; i < len; i++) {
            result.append(random.nextInt(10));
        }

        return result.toString();
    }
}
