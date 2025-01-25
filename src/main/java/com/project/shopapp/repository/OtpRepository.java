package com.project.shopapp.repository;

import com.project.shopapp.entity.OtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpEntity, Long> {
    Optional<OtpEntity> findByEmailAndAndOtpCode(String email, String otpCode);
}
