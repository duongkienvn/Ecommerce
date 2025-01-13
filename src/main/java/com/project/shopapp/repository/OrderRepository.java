package com.project.shopapp.repository;

import com.project.shopapp.entity.OrderEntity;
import com.project.shopapp.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findOrderEntitiesByUserId(Long userId);
}
