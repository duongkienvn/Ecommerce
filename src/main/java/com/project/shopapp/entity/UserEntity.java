package com.project.shopapp.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "users")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "fullname", length = 100)
    String fullName;

    @Column(name = "phone_number", length = 10,
            nullable = false, unique = true,
            columnDefinition = "VARCHAR(255) COLLATE utf8mp4_unicode_ci")
    String phoneNumber;

    @Column(name = "address", length = 200)
    String address;

    @Column(name = "password", length = 200, nullable = false)
    String password;

    @Column(name = "is_active", insertable = false)
    int active;

    @Column(name = "date_of_birth")
    LocalDate dateOfBirth;

    @Column(name = "facebook_account_id")
    int facebookAccountId;

    @Column(name = "google_account_id")
    int googleAccountId;

    @ManyToOne
    @JoinColumn(name = "role_id")
    RoleEntity roleEntity;
}
