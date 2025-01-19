package com.project.shopapp.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.entity.OrderEntity;
import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class OrderResponse {
    @JsonProperty("fullname")
    String fullName;

    String email;

    @JsonProperty("phone_number")
    String phoneNumber;

    String address;

    String note;

    @JsonProperty("order_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    LocalDateTime orderDate;

    String status;

    @JsonProperty("total_money")
    Double totalMoney;

    @JsonProperty("shipping_method")
    String shippingMethod;

    @JsonProperty("shipping_address")
    String shippingAddress;

    @JsonProperty("shipping_date")
    LocalDate shippingDate;

    @JsonProperty("tracking_number")
    String trackingNumber;

    @JsonProperty("payment_method")
    String paymentMethod;

    @JsonProperty("user_id")
    Long userId;
}
