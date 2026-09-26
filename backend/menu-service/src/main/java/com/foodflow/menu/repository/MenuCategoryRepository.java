package com.foodflow.menu.repository;

import com.foodflow.menu.entity.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MenuCategoryRepository extends JpaRepository<MenuCategory, UUID> {
    List<MenuCategory> findByRestaurantIdOrderByDisplayOrderAsc(UUID restaurantId);
}
