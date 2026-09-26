package com.foodflow.menu.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MenuItemResponse {
    private UUID id;
    private UUID restaurantId;
    private UUID categoryId;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private Boolean isAvailable;
    private Boolean isVegetarian;
    private Integer preparationTimeMinutes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
