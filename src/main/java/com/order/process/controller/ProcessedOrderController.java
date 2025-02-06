package com.order.process.controller;


import com.order.process.model.OrderResponse;
import com.order.process.service.ProcessedOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/processed-orders")
public class ProcessedOrderController {

    private final ProcessedOrderService processedOrderService;

    public ProcessedOrderController(ProcessedOrderService processedOrderService) {
        this.processedOrderService = processedOrderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getProcessedOrders() {
        List<OrderResponse> orders = processedOrderService.getProcessedOrders();
        return ResponseEntity.ok(orders);
    }
}
