package com.project.shopapp.repository;

import com.project.shopapp.entity.ProductEntity;
import com.project.shopapp.repository.custom.ProductRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long>, ProductRepositoryCustom {
}
