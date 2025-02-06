package com.order.process.repository;

import com.order.process.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository  extends JpaRepository<Order, Long> {
    Optional<Order> findByExternalOrderId(String externalOrderId);
}
