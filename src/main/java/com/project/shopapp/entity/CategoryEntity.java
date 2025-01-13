package com.project.shopapp.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "name")
    String name;

    @OneToMany(mappedBy = "category",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE}
            , orphanRemoval = true)
    List<ProductEntity> productEntities;
}
