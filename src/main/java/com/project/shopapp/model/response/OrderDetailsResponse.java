package com.project.shopapp.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.entity.OrderDetailsEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class OrderDetailsResponse {
    private Long id;

    @JsonProperty("order_id")
    private Long orderId;

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("price")
    private Float price;

    @JsonProperty("number_of_products")
    private int numberOfProducts;

    @JsonProperty("total_money")
    private Float totalMoney;

    private String color;

    public static OrderDetailsResponse fromOrderDetails(OrderDetailsEntity orderDetailsEntity) {
        return OrderDetailsResponse.builder()
                .id(orderDetailsEntity.getId())
                .orderId(orderDetailsEntity.getOrder().getId())
                .productId(orderDetailsEntity.getProduct().getId())
                .color(orderDetailsEntity.getColor())
                .price(orderDetailsEntity.getPrice())
                .totalMoney(orderDetailsEntity.getTotalMoney())
                .numberOfProducts(orderDetailsEntity.getNumberOfProducts())
                .build();
    }
}
