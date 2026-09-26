package com.foodflow.order.service;

import com.foodflow.order.dto.CreateOrderRequest;
import com.foodflow.order.dto.OrderResponse;
import com.foodflow.order.dto.UpdateOrderStatusRequest;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request);
    OrderResponse getOrderById(UUID id);
    List<OrderResponse> getOrdersByUser(UUID userId);
    List<OrderResponse> getOrdersByRestaurant(UUID restaurantId);
    OrderResponse updateOrderStatus(UUID id, UpdateOrderStatusRequest request);
    OrderResponse cancelOrder(UUID id);
}
