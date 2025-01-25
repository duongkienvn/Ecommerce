package com.project.shopapp.repository;

import com.project.shopapp.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepostiory extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);

    @Transactional
    @Modifying
    @Query("update UserEntity u set u.password = ?2 where u.email = ?1")
    void updateByEmailAndPassword(String email, String password);

    @Transactional
    @Modifying
    @Query("update UserEntity u set u.password = ?2 where u.id = ?1")
    void updateById(Long id, String password);
}
