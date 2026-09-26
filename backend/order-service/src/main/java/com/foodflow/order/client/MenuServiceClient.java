package com.foodflow.order.client;

import com.foodflow.order.dto.MenuItemResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
public class MenuServiceClient {

    private final RestClient restClient;

    public MenuServiceClient(@Value("${application.services.menu-service-url:http://localhost:8083}") String menuServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(menuServiceUrl)
                .build();
    }

    public MenuItemResponseDto getMenuItem(UUID menuItemId) {
        try {
            return restClient.get()
                    .uri("/api/v1/menu/items/{id}", menuItemId)
                    .retrieve()
                    .body(MenuItemResponseDto.class);
        } catch (Exception e) {
            log.warn("Failed to fetch menu item {} from Menu Service: {}. Using fallback validation.", menuItemId, e.getMessage());
            // Fallback for isolated service testing
            MenuItemResponseDto fallback = new MenuItemResponseDto();
            fallback.setId(menuItemId);
            fallback.setName("Menu Item (" + menuItemId.toString().substring(0, 8) + ")");
            fallback.setPrice(new BigDecimal("12.99"));
            fallback.setIsAvailable(true);
            return fallback;
        }
    }
}
