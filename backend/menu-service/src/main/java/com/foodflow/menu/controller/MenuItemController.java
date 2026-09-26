package com.foodflow.menu.controller;

import com.foodflow.menu.dto.CreateMenuItemRequest;
import com.foodflow.menu.dto.MenuItemResponse;
import com.foodflow.menu.dto.UpdateMenuItemRequest;
import com.foodflow.menu.service.MenuService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/menu/items")
public class MenuItemController {

    private final MenuService menuService;

    public MenuItemController(MenuService menuService) {
        this.menuService = menuService;
    }

    @PostMapping
    public ResponseEntity<MenuItemResponse> createMenuItem(
            @Valid @RequestBody CreateMenuItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(menuService.createMenuItem(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItemResponse> getMenuItemById(
            @PathVariable UUID id) {
        return ResponseEntity.ok(menuService.getMenuItemById(id));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<MenuItemResponse>> getMenuItemsByRestaurant(
            @PathVariable UUID restaurantId,
            @RequestParam(required = false) Boolean availableOnly) {
        return ResponseEntity.ok(menuService.getMenuItemsByRestaurant(restaurantId, availableOnly));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<MenuItemResponse>> getMenuItemsByCategory(
            @PathVariable UUID categoryId) {
        return ResponseEntity.ok(menuService.getMenuItemsByCategory(categoryId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItemResponse> updateMenuItem(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateMenuItemRequest request) {
        return ResponseEntity.ok(menuService.updateMenuItem(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(
            @PathVariable UUID id) {
        menuService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }
}
