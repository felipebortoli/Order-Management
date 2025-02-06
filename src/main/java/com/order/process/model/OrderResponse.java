package com.order.process.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderResponse {

    private String externalOrderId;
    private BigDecimal totalAmount;
    private String status;
    private List<OrderItemDTO> items;


    @Data
    public static class OrderItemDTO {
        private String productId;
        private BigDecimal price;
        private int quantity;
    }
}
