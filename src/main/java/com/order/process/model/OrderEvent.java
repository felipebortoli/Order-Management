package com.order.process.model;


import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderEvent {
    private BigDecimal totalAmount;
    private String status;
    private String externalOrderId;
    private List<Item> items;
}
