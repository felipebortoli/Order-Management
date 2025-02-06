package com.order.process.listeners;


import com.order.process.model.OrderEvent;
import com.order.process.model.OrderResponse;
import com.order.process.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class OrderKafkaListener {

    private final OrderService orderService;

    private final KafkaTemplate<String, OrderResponse> kafkaTemplate;

    @KafkaListener(topics = "orders-received")
    public void receiveOrder(OrderEvent orderEvent) {
        try {
            OrderResponse processedOrder = orderService.processOrder(orderEvent);
            kafkaTemplate.send("orders-processed", processedOrder);
        } catch (IllegalStateException e) {
            System.err.println("Erro ao processar pedido: " + e.getMessage());
        }catch (Exception e) {
            System.err.println("Erro ao processar pedido: " + e.getMessage());
        }
    }
}
