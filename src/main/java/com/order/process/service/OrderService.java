package com.order.process.service;


import com.order.process.model.Order;
import com.order.process.model.OrderEvent;
import com.order.process.model.OrderItem;
import com.order.process.model.OrderResponse;
import com.order.process.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;



    public OrderService(OrderRepository orderRepository, RedisTemplate<String, String> redisTemplate, KafkaTemplate<String, OrderEvent> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Retry(name = "orderService")
    @CircuitBreaker(name = "orderProcessingCircuitBreaker", fallbackMethod = "fallbackProcessOrder")
    public OrderResponse processOrder(OrderEvent orderEvent) {
        String cacheKey = orderEvent.getExternalOrderId();


        if (Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey))) {
            throw new IllegalStateException("Pedido já processado");
        }

        BigDecimal totalAmount = orderEvent.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setTotalAmount(totalAmount);
        order.setStatus("PROCESSED");
        order.setExternalOrderId(cacheKey);
        List<OrderItem> orderItems = orderEvent.getItems().stream()
                .map(item -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setProductId(item.getProductId());
                    orderItem.setPrice(item.getPrice());
                    orderItem.setQuantity(item.getQuantity());
                    orderItem.setOrder(order);
                    return orderItem;
                })
                .collect(Collectors.toList());

        order.setItems(orderItems);

        orderRepository.save(order);
        redisTemplate.opsForValue().set(cacheKey, "PROCESSED", Duration.ofHours(1));
        return mapToDTO(order);
    }

    public OrderResponse fallbackProcessOrder(OrderEvent orderEvent,Throwable throwable) {
        System.err.println("Error processing order: " + throwable.getMessage());
        return new OrderResponse();
    }

    public OrderResponse findByExternalOrderId(String externalOrderId) {
        Optional<Order> orderOptional = orderRepository.findByExternalOrderId(externalOrderId);
        if (!orderOptional.isPresent()) {
            return null;
        }
        Order order = orderOptional.get();
        OrderResponse orderResponse = mapToDTO(order);

        return orderResponse;
    }

    private OrderResponse mapToDTO(Order order) {
        OrderResponse dto = new OrderResponse();
        dto.setExternalOrderId(order.getExternalOrderId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setItems(order.getItems().stream()
                .map(item -> {
                    OrderResponse.OrderItemDTO itemDTO = new OrderResponse.OrderItemDTO();
                    itemDTO.setProductId(item.getProductId());
                    itemDTO.setPrice(item.getPrice());
                    itemDTO.setQuantity(item.getQuantity());
                    return itemDTO;
                })
                .collect(Collectors.toList()));

        return dto;
    }


    public void createOrder(OrderEvent orderEvent) {
        String cacheKey = orderEvent.getExternalOrderId();
        if (Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey))) {
            throw new IllegalStateException("Pedido já processado");
        }
        kafkaTemplate.send("orders-received", orderEvent);
    }

}
