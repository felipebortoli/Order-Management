package com.order.process.listeners;

import com.order.process.model.OrderResponse;
import com.order.process.service.ProcessedOrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderProcessedListener {

    private final ProcessedOrderService service;

    public OrderProcessedListener(ProcessedOrderService service) {
        this.service = service;
    }

    @KafkaListener(topics = "orders-processed", groupId = "order-processed-group")
    public void listen(OrderResponse orderResponse) {
        service.addProcessedOrder(orderResponse);
    }
}
