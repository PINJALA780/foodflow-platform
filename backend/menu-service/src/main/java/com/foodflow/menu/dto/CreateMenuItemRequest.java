package com.foodflow.menu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateMenuItemRequest {

    @NotNull(message = "Restaurant ID is required")
    private UUID restaurantId;

    private UUID categoryId;

    @NotBlank(message = "Item name is required")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    private String imageUrl;

    private Boolean isAvailable = true;

    private Boolean isVegetarian = false;

    private Integer preparationTimeMinutes = 15;
}
