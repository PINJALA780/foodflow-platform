package com.foodflow.restaurant.repository;

import com.foodflow.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {

    List<Restaurant> findByCityIgnoreCase(String city);

    List<Restaurant> findByCategoryIgnoreCase(String category);

    List<Restaurant> findByIsOpenTrue();
}
