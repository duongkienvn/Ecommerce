package com.project.shopapp.service.impl;

import com.project.shopapp.entity.OtpEntity;
import com.project.shopapp.repository.OtpRepository;
import com.project.shopapp.service.IOtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService implements IOtpService {
    private final JavaMailSender mailSender;
    private final OtpRepository otpRepository;

    private String generateOtp() {
        Random random = new Random();
        int otp = random.nextInt(100_000, 999_999);

        return String.valueOf(otp);
    }

    @Override
    public void generateAndSendOtp(String email) {
        String otpCode = generateOtp();

        OtpEntity otp = OtpEntity.builder()
                .email(email)
                .otpCode(otpCode)
                .expirationTime(LocalDateTime.now().plusMinutes(2))
                .build();
        otpRepository.save(otp);
        sendEmail(email, otpCode);
    }

    @Async
    private void sendEmail(String to, String otpCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Reset Password OTP");
        message.setText("Your OTP is: " + otpCode + ". Is is valid for 2 minutes.\nPlease don't share it with anyone!");
        mailSender.send(message);
    }

    @Override
    public boolean isValidOtp(String email, String otpCode) {
        Optional<OtpEntity> optionalOtp = otpRepository.findByEmailAndAndOtpCode(email, otpCode);
        if (optionalOtp.isPresent()) {
            OtpEntity otp = optionalOtp.get();
            return otp.getExpirationTime().isAfter(LocalDateTime.now());
        }

        return false;
    }
}
