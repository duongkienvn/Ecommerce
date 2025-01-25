package com.project.shopapp.service;

public interface IOtpService {
    void generateAndSendOtp(String email);
    boolean isValidOtp(String email, String otpCode);
}
