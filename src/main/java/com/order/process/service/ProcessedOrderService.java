package com.order.process.service;

import com.order.process.model.OrderResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProcessedOrderService {
    private final List<OrderResponse> processedOrders = new ArrayList<>();

    public void addProcessedOrder(OrderResponse orderResponse) {
        synchronized (processedOrders) {
            processedOrders.add(orderResponse);
        }
    }

    public List<OrderResponse> getProcessedOrders() {
        synchronized (processedOrders) {
            return new ArrayList<>(processedOrders);
        }
    }
}
