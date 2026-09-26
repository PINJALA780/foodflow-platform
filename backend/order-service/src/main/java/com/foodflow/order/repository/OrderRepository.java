package com.foodflow.order.repository;

import com.foodflow.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUserIdOrderByCreatedAtDesc(UUID userId);
    List<Order> findByRestaurantIdOrderByCreatedAtDesc(UUID restaurantId);
}
