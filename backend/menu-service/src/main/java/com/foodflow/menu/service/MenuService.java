package com.foodflow.menu.service;

import com.foodflow.menu.dto.*;

import java.util.List;
import java.util.UUID;

public interface MenuService {
    CategoryResponse createCategory(CreateCategoryRequest request);
    List<CategoryResponse> getCategoriesByRestaurant(UUID restaurantId);

    MenuItemResponse createMenuItem(CreateMenuItemRequest request);
    MenuItemResponse getMenuItemById(UUID id);
    List<MenuItemResponse> getMenuItemsByRestaurant(UUID restaurantId, Boolean availableOnly);
    List<MenuItemResponse> getMenuItemsByCategory(UUID categoryId);
    MenuItemResponse updateMenuItem(UUID id, UpdateMenuItemRequest request);
    void deleteMenuItem(UUID id);
}
