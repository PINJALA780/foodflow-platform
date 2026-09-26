package com.foodflow.order.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class MenuItemResponseDto {
    private UUID id;
    private UUID restaurantId;
    private String name;
    private BigDecimal price;
    private Boolean isAvailable;
}
