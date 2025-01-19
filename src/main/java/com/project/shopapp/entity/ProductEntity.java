package com.project.shopapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
public class ProductEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "name", nullable = false, length = 350)
    String name;

    @Column(name = "price")
    Float price;

    @Column(name = "description")
    String description;

    @Column(name = "thumbnail", length = 300)
    String thumbnail;

    @ManyToOne
    @JoinColumn(name = "category_id")
    CategoryEntity category;

    @OneToMany(mappedBy = "product", orphanRemoval = true, cascade = CascadeType.REMOVE)
    List<OrderDetailsEntity> orderDetailsEntityList;
}
