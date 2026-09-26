package com.foodflow.restaurant.controller;

import com.foodflow.restaurant.dto.CreateRestaurantRequest;
import com.foodflow.restaurant.dto.RestaurantResponse;
import com.foodflow.restaurant.dto.UpdateRestaurantRequest;
import com.foodflow.restaurant.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @PostMapping
    public ResponseEntity<RestaurantResponse> createRestaurant(
            @Valid @RequestBody CreateRestaurantRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(restaurantService.createRestaurant(request));
    }

    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getRestaurants(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean open) {

        if (city != null && !city.isBlank()) {
            return ResponseEntity.ok(
                    restaurantService.getRestaurantsByCity(city)
            );
        }

        if (category != null && !category.isBlank()) {
            return ResponseEntity.ok(
                    restaurantService.getRestaurantsByCategory(category)
            );
        }

        if (Boolean.TRUE.equals(open)) {
            return ResponseEntity.ok(
                    restaurantService.getOpenRestaurants()
            );
        }

        return ResponseEntity.ok(
                restaurantService.getAllRestaurants()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getRestaurantById(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                restaurantService.getRestaurantById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantResponse> updateRestaurant(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRestaurantRequest request) {

        return ResponseEntity.ok(
                restaurantService.updateRestaurant(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(
            @PathVariable UUID id) {

        restaurantService.deleteRestaurant(id);

        return ResponseEntity.noContent().build();
    }
}
