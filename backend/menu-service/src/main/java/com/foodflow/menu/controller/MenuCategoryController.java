package com.foodflow.menu.controller;

import com.foodflow.menu.dto.CategoryResponse;
import com.foodflow.menu.dto.CreateCategoryRequest;
import com.foodflow.menu.service.MenuService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/menu/categories")
public class MenuCategoryController {

    private final MenuService menuService;

    public MenuCategoryController(MenuService menuService) {
        this.menuService = menuService;
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CreateCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(menuService.createCategory(request));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<CategoryResponse>> getCategoriesByRestaurant(
            @PathVariable UUID restaurantId) {
        return ResponseEntity.ok(menuService.getCategoriesByRestaurant(restaurantId));
    }
}
