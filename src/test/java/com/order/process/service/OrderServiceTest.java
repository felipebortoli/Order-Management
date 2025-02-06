package com.order.process.service;

import com.order.process.model.Item;
import com.order.process.model.Order;
import com.order.process.model.OrderEvent;
import com.order.process.model.OrderResponse;
import com.order.process.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ActiveProfiles("test")
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @InjectMocks
    private OrderService orderService;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    public void testProcessOrder_ShouldCreateOrder_WhenNotDuplicated() {
        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setExternalOrderId("1234");
        orderEvent.setTotalAmount(BigDecimal.valueOf(100));

        Item item = new Item();
        item.setProductId("prod-1");
        item.setPrice(BigDecimal.valueOf(50));
        item.setQuantity(2);
        orderEvent.setItems(Collections.singletonList(item));

        when(redisTemplate.hasKey("1234")).thenReturn(false);

        when(orderRepository.save(any(Order.class))).thenReturn(new Order());

        doNothing().when(valueOperations).set(eq("1234"), eq("PROCESSED"), eq(Duration.ofHours(1)));

        OrderResponse result = orderService.processOrder(orderEvent);

        assertNotNull(result);
        verify(valueOperations).set("1234", "PROCESSED", Duration.ofHours(1));

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testProcessOrder_ShouldThrowException_WhenDuplicated() {
        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setExternalOrderId("1234");
        orderEvent.setItems(Collections.emptyList());


        when(redisTemplate.hasKey("1234")).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> orderService.processOrder(orderEvent));
    }
}
