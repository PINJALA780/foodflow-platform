package com.foodflow.order.service;

import com.foodflow.order.client.MenuServiceClient;
import com.foodflow.order.dto.CreateOrderRequest;
import com.foodflow.order.dto.OrderItemRequest;
import com.foodflow.order.dto.MenuItemResponseDto;
import com.foodflow.order.dto.OrderResponse;
import com.foodflow.order.entity.Order;
import com.foodflow.order.entity.OrderStatus;
import com.foodflow.order.entity.PaymentMethod;
import com.foodflow.order.entity.PaymentStatus;
import com.foodflow.order.exception.BadRequestException;
import com.foodflow.order.repository.OrderRepository;
import com.foodflow.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MenuServiceClient menuServiceClient;

    @InjectMocks
    private OrderServiceImpl orderService;

    private UUID sampleUserId;
    private UUID sampleRestaurantId;
    private UUID sampleMenuItemId;
    private MenuItemResponseDto sampleMenuItem;

    @BeforeEach
    void setUp() {
        sampleUserId = UUID.randomUUID();
        sampleRestaurantId = UUID.randomUUID();
        sampleMenuItemId = UUID.randomUUID();

        sampleMenuItem = new MenuItemResponseDto();
        sampleMenuItem.setId(sampleMenuItemId);
        sampleMenuItem.setRestaurantId(sampleRestaurantId);
        sampleMenuItem.setName("Chicken Biryani");
        sampleMenuItem.setPrice(new BigDecimal("10.00"));
        sampleMenuItem.setIsAvailable(true);
    }

    @Test
    void createOrder_Success() {
        CreateOrderRequest req = new CreateOrderRequest();
        req.setUserId(sampleUserId);
        req.setRestaurantId(sampleRestaurantId);
        req.setDeliveryAddress("123 Main St");
        req.setPaymentMethod(PaymentMethod.CARD);

        OrderItemRequest itemReq = new OrderItemRequest();
        itemReq.setMenuItemId(sampleMenuItemId);
        itemReq.setQuantity(2);
        req.setItems(List.of(itemReq));

        when(menuServiceClient.getMenuItem(sampleMenuItemId)).thenReturn(sampleMenuItem);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(UUID.randomUUID());
            return o;
        });

        OrderResponse res = orderService.createOrder(req);

        assertNotNull(res);
        assertEquals(sampleUserId, res.getUserId());
        assertEquals(new BigDecimal("20.00"), res.getSubtotal()); // 10.00 * 2
        assertEquals(new BigDecimal("3.99"), res.getDeliveryFee());
        assertEquals(new BigDecimal("1.00"), res.getTax()); // 5% of 20.00 = 1.00
        assertEquals(new BigDecimal("24.99"), res.getTotalAmount());
        assertEquals(OrderStatus.PLACED, res.getStatus());
        assertEquals(PaymentStatus.PENDING, res.getPaymentStatus());
    }

    @Test
    void createOrder_UnavailableItem_ThrowsException() {
        sampleMenuItem.setIsAvailable(false);

        CreateOrderRequest req = new CreateOrderRequest();
        req.setUserId(sampleUserId);
        req.setRestaurantId(sampleRestaurantId);
        req.setDeliveryAddress("123 Main St");
        req.setPaymentMethod(PaymentMethod.COD);

        OrderItemRequest itemReq = new OrderItemRequest();
        itemReq.setMenuItemId(sampleMenuItemId);
        itemReq.setQuantity(1);
        req.setItems(List.of(itemReq));

        when(menuServiceClient.getMenuItem(sampleMenuItemId)).thenReturn(sampleMenuItem);

        assertThrows(BadRequestException.class, () -> orderService.createOrder(req));
    }

    @Test
    void cancelOrder_Success() {
        UUID orderId = UUID.randomUUID();
        Order existingOrder = new Order();
        existingOrder.setId(orderId);
        existingOrder.setStatus(OrderStatus.PLACED);
        existingOrder.setPaymentStatus(PaymentStatus.PENDING);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse res = orderService.cancelOrder(orderId);

        assertEquals(OrderStatus.CANCELLED, res.getStatus());
    }

    @Test
    void cancelOrder_Delivered_ThrowsException() {
        UUID orderId = UUID.randomUUID();
        Order existingOrder = new Order();
        existingOrder.setId(orderId);
        existingOrder.setStatus(OrderStatus.DELIVERED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));

        assertThrows(BadRequestException.class, () -> orderService.cancelOrder(orderId));
    }
}
