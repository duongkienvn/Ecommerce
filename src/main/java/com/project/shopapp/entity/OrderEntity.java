package com.project.shopapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "fullname", length = 100)
    String fullName;

    @Column(name = "email", length = 100)
    String email;

    @Column(name = "phone_number", length = 100, nullable = false)
    String phoneNumber;

    @Column(name = "address", length = 100)
    String address;

    @Column(name = "note", length = 100)
    String note;

    @Column(name = "order_date")
    Date orderDate;

    @Column(name = "status")
    String status;

    @Column(name = "total_money")
    Double totalMoney;

    @Column(name = "shipping_method")
    String shippingMethod;

    @Column(name = "shipping_address")
    String shippingAddress;

    @Column(name = "shipping_date")
    LocalDate shippingDate;

    @Column(name = "tracking_number")
    String trackingNumber;

    @Column(name = "payment_method")
    String paymentMethod;

    @Column(name = "active")
    Boolean active;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    UserEntity user;
}
