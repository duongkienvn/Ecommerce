package com.project.shopapp.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDto {
    @JsonProperty("user_id")
    @Min(value = 1, message = "User's id must be > 0")
    Long userId;

    @JsonProperty("fullname")
    String fullName;

    String email;

    @JsonProperty("phone_number")
    @NotBlank(message = "Phone number is required!")
    @Size(min = 5, message = "Phone number must be at least 5 character!")
    String phoneNumber;

    String address;

    String note;

    @JsonProperty("total_money")
    @Min(value = 0, message = "Total money must be >= 0")
    Double totalMoney;

    @JsonProperty("shipping_method")
    String shippingMethod;

    @JsonProperty("shipping_address")
    String shippingAddress;

    @JsonProperty("shipping_date")
    LocalDate shippingDate;

    @JsonProperty("payment_method")
    String paymentMethod;
}
