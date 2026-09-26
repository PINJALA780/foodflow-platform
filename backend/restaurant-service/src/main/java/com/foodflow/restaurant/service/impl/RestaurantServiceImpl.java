package com.foodflow.restaurant.service.impl;

import com.foodflow.restaurant.dto.CreateRestaurantRequest;
import com.foodflow.restaurant.dto.RestaurantResponse;
import com.foodflow.restaurant.dto.UpdateRestaurantRequest;
import com.foodflow.restaurant.entity.Restaurant;
import com.foodflow.restaurant.exception.RestaurantNotFoundException;
import com.foodflow.restaurant.repository.RestaurantRepository;
import com.foodflow.restaurant.service.RestaurantService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantServiceImpl(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public RestaurantResponse createRestaurant(CreateRestaurantRequest request) {

        Restaurant restaurant = new Restaurant();

        restaurant.setName(request.getName());
        restaurant.setDescription(request.getDescription());
        restaurant.setCategory(request.getCategory());
        restaurant.setAddress(request.getAddress());
        restaurant.setCity(request.getCity());
        restaurant.setPhone(request.getPhone());
        restaurant.setImageUrl(request.getImageUrl());
        restaurant.setRating(request.getRating());
        restaurant.setDeliveryTimeMinutes(request.getDeliveryTimeMinutes());
        restaurant.setIsOpen(
                request.getIsOpen() != null ? request.getIsOpen() : true
        );

        return toResponse(restaurantRepository.save(restaurant));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getAllRestaurants() {
        return restaurantRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RestaurantResponse getRestaurantById(UUID id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException(id));

        return toResponse(restaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getRestaurantsByCity(String city) {
        return restaurantRepository.findByCityIgnoreCase(city)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getRestaurantsByCategory(String category) {
        return restaurantRepository.findByCategoryIgnoreCase(category)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getOpenRestaurants() {
        return restaurantRepository.findByIsOpenTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public RestaurantResponse updateRestaurant(
            UUID id,
            UpdateRestaurantRequest request
    ) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException(id));

        restaurant.setName(request.getName());
        restaurant.setDescription(request.getDescription());
        restaurant.setCategory(request.getCategory());
        restaurant.setAddress(request.getAddress());
        restaurant.setCity(request.getCity());
        restaurant.setPhone(request.getPhone());
        restaurant.setImageUrl(request.getImageUrl());
        restaurant.setRating(request.getRating());
        restaurant.setDeliveryTimeMinutes(request.getDeliveryTimeMinutes());

        if (request.getIsOpen() != null) {
            restaurant.setIsOpen(request.getIsOpen());
        }

        return toResponse(restaurantRepository.save(restaurant));
    }

    @Override
    public void deleteRestaurant(UUID id) {

        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException(id));

        restaurantRepository.delete(restaurant);
    }

    private RestaurantResponse toResponse(Restaurant restaurant) {

        RestaurantResponse response = new RestaurantResponse();

        response.setId(restaurant.getId());
        response.setName(restaurant.getName());
        response.setDescription(restaurant.getDescription());
        response.setCategory(restaurant.getCategory());
        response.setAddress(restaurant.getAddress());
        response.setCity(restaurant.getCity());
        response.setPhone(restaurant.getPhone());
        response.setImageUrl(restaurant.getImageUrl());
        response.setRating(restaurant.getRating());
        response.setDeliveryTimeMinutes(
                restaurant.getDeliveryTimeMinutes()
        );
        response.setIsOpen(restaurant.getIsOpen());
        response.setCreatedAt(restaurant.getCreatedAt());
        response.setUpdatedAt(restaurant.getUpdatedAt());

        return response;
    }
}
