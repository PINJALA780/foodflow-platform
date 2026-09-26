package com.foodflow.order.service.impl;

import com.foodflow.order.client.MenuServiceClient;
import com.foodflow.order.dto.*;
import com.foodflow.order.entity.*;
import com.foodflow.order.exception.BadRequestException;
import com.foodflow.order.exception.ResourceNotFoundException;
import com.foodflow.order.repository.OrderRepository;
import com.foodflow.order.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final MenuServiceClient menuServiceClient;

    public OrderServiceImpl(OrderRepository orderRepository, MenuServiceClient menuServiceClient) {
        this.orderRepository = orderRepository;
        this.menuServiceClient = menuServiceClient;
    }

    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BadRequestException("Order must contain at least one item");
        }

        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setRestaurantId(request.getRestaurantId());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setStatus(OrderStatus.PLACED);

        BigDecimal subtotal = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.getItems()) {
            MenuItemResponseDto menuItem = menuServiceClient.getMenuItem(itemReq.getMenuItemId());
            if (menuItem == null) {
                throw new BadRequestException("Invalid menu item: " + itemReq.getMenuItemId());
            }
            if (Boolean.FALSE.equals(menuItem.getIsAvailable())) {
                throw new BadRequestException("Menu item is currently unavailable: " + menuItem.getName());
            }

            BigDecimal unitPrice = menuItem.getPrice() != null ? menuItem.getPrice() : new BigDecimal("10.00");
            BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity()));

            OrderItem orderItem = new OrderItem();
            orderItem.setMenuItemId(menuItem.getId());
            orderItem.setItemName(menuItem.getName());
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setUnitPrice(unitPrice);
            orderItem.setTotalPrice(itemTotal);

            order.addItem(orderItem);
            subtotal = subtotal.add(itemTotal);
        }

        BigDecimal deliveryFee = new BigDecimal("3.99");
        BigDecimal tax = subtotal.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = subtotal.add(deliveryFee).add(tax);

        order.setSubtotal(subtotal);
        order.setDeliveryFee(deliveryFee);
        order.setTax(tax);
        order.setTotalAmount(totalAmount);

        return toOrderResponse(orderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        return toOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUser(UUID userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toOrderResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByRestaurant(UUID restaurantId) {
        return orderRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurantId)
                .stream()
                .map(this::toOrderResponse)
                .toList();
    }

    @Override
    public OrderResponse updateOrderStatus(UUID id, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));

        order.setStatus(request.getStatus());
        if (request.getStatus() == OrderStatus.DELIVERED && order.getPaymentStatus() == PaymentStatus.PENDING) {
            order.setPaymentStatus(PaymentStatus.SUCCESS);
        }

        return toOrderResponse(orderRepository.save(order));
    }

    @Override
    public OrderResponse cancelOrder(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.OUT_FOR_DELIVERY) {
            throw new BadRequestException("Order cannot be cancelled in its current state: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);
        if (order.getPaymentStatus() == PaymentStatus.SUCCESS) {
            order.setPaymentStatus(PaymentStatus.REFUNDED);
        }

        return toOrderResponse(orderRepository.save(order));
    }

    private OrderResponse toOrderResponse(Order order) {
        OrderResponse res = new OrderResponse();
        res.setId(order.getId());
        res.setUserId(order.getUserId());
        res.setRestaurantId(order.getRestaurantId());
        res.setDeliveryAddress(order.getDeliveryAddress());
        res.setSubtotal(order.getSubtotal());
        res.setDeliveryFee(order.getDeliveryFee());
        res.setTax(order.getTax());
        res.setTotalAmount(order.getTotalAmount());
        res.setPaymentMethod(order.getPaymentMethod());
        res.setPaymentStatus(order.getPaymentStatus());
        res.setStatus(order.getStatus());
        res.setCreatedAt(order.getCreatedAt());
        res.setUpdatedAt(order.getUpdatedAt());

        if (order.getItems() != null) {
            res.setItems(order.getItems().stream().map(item -> {
                OrderItemResponse itemRes = new OrderItemResponse();
                itemRes.setId(item.getId());
                itemRes.setMenuItemId(item.getMenuItemId());
                itemRes.setItemName(item.getItemName());
                itemRes.setQuantity(item.getQuantity());
                itemRes.setUnitPrice(item.getUnitPrice());
                itemRes.setTotalPrice(item.getTotalPrice());
                return itemRes;
            }).toList());
        }

        return res;
    }
}
