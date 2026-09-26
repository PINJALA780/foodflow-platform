package com.foodflow.restaurant.service;

import com.foodflow.restaurant.dto.CreateRestaurantRequest;
import com.foodflow.restaurant.dto.RestaurantResponse;
import com.foodflow.restaurant.dto.UpdateRestaurantRequest;

import java.util.List;
import java.util.UUID;

public interface RestaurantService {

    RestaurantResponse createRestaurant(CreateRestaurantRequest request);

    List<RestaurantResponse> getAllRestaurants();

    RestaurantResponse getRestaurantById(UUID id);

    List<RestaurantResponse> getRestaurantsByCity(String city);

    List<RestaurantResponse> getRestaurantsByCategory(String category);

    List<RestaurantResponse> getOpenRestaurants();

    RestaurantResponse updateRestaurant(UUID id, UpdateRestaurantRequest request);

    void deleteRestaurant(UUID id);
}
