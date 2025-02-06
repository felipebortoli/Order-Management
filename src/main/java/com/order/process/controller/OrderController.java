package com.order.process.controller;


import com.order.process.model.OrderEvent;
import com.order.process.model.OrderResponse;
import com.order.process.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;


    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{externalOrderId}")
    public ResponseEntity<OrderResponse> getOrderByExternalId(@PathVariable String externalOrderId) {
        OrderResponse order = orderService.findByExternalOrderId(externalOrderId);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(order);
    }

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody OrderEvent orderEvent) {
        try {
            orderService.createOrder(orderEvent);
            return ResponseEntity.ok("Pedido enviado " + orderEvent.getExternalOrderId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar o pedido: " + e.getMessage());
        }
    }

}
