package com.foodflow.menu.service.impl;

import com.foodflow.menu.dto.*;
import com.foodflow.menu.entity.MenuCategory;
import com.foodflow.menu.entity.MenuItem;
import com.foodflow.menu.exception.ResourceNotFoundException;
import com.foodflow.menu.repository.MenuCategoryRepository;
import com.foodflow.menu.repository.MenuItemRepository;
import com.foodflow.menu.service.MenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class MenuServiceImpl implements MenuService {

    private final MenuCategoryRepository categoryRepository;
    private final MenuItemRepository menuItemRepository;

    public MenuServiceImpl(MenuCategoryRepository categoryRepository, MenuItemRepository menuItemRepository) {
        this.categoryRepository = categoryRepository;
        this.menuItemRepository = menuItemRepository;
    }

    @Override
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        MenuCategory category = new MenuCategory();
        category.setRestaurantId(request.getRestaurantId());
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);

        return toCategoryResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoriesByRestaurant(UUID restaurantId) {
        return categoryRepository.findByRestaurantIdOrderByDisplayOrderAsc(restaurantId)
                .stream()
                .map(this::toCategoryResponse)
                .toList();
    }

    @Override
    public MenuItemResponse createMenuItem(CreateMenuItemRequest request) {
        MenuItem item = new MenuItem();
        item.setRestaurantId(request.getRestaurantId());
        item.setCategoryId(request.getCategoryId());
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setImageUrl(request.getImageUrl());
        item.setIsAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true);
        item.setIsVegetarian(request.getIsVegetarian() != null ? request.getIsVegetarian() : false);
        item.setPreparationTimeMinutes(request.getPreparationTimeMinutes() != null ? request.getPreparationTimeMinutes() : 15);

        return toItemResponse(menuItemRepository.save(item));
    }

    @Override
    @Transactional(readOnly = true)
    public MenuItemResponse getMenuItemById(UUID id) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", id));
        return toItemResponse(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getMenuItemsByRestaurant(UUID restaurantId, Boolean availableOnly) {
        List<MenuItem> items = Boolean.TRUE.equals(availableOnly)
                ? menuItemRepository.findByRestaurantIdAndIsAvailableTrue(restaurantId)
                : menuItemRepository.findByRestaurantId(restaurantId);

        return items.stream().map(this::toItemResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getMenuItemsByCategory(UUID categoryId) {
        return menuItemRepository.findByCategoryId(categoryId)
                .stream()
                .map(this::toItemResponse)
                .toList();
    }

    @Override
    public MenuItemResponse updateMenuItem(UUID id, UpdateMenuItemRequest request) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", id));

        if (request.getCategoryId() != null) {
            item.setCategoryId(request.getCategoryId());
        }
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setImageUrl(request.getImageUrl());

        if (request.getIsAvailable() != null) {
            item.setIsAvailable(request.getIsAvailable());
        }
        if (request.getIsVegetarian() != null) {
            item.setIsVegetarian(request.getIsVegetarian());
        }
        if (request.getPreparationTimeMinutes() != null) {
            item.setPreparationTimeMinutes(request.getPreparationTimeMinutes());
        }

        return toItemResponse(menuItemRepository.save(item));
    }

    @Override
    public void deleteMenuItem(UUID id) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", id));
        menuItemRepository.delete(item);
    }

    private CategoryResponse toCategoryResponse(MenuCategory cat) {
        CategoryResponse res = new CategoryResponse();
        res.setId(cat.getId());
        res.setRestaurantId(cat.getRestaurantId());
        res.setName(cat.getName());
        res.setDescription(cat.getDescription());
        res.setDisplayOrder(cat.getDisplayOrder());
        res.setCreatedAt(cat.getCreatedAt());
        res.setUpdatedAt(cat.getUpdatedAt());
        return res;
    }

    private MenuItemResponse toItemResponse(MenuItem item) {
        MenuItemResponse res = new MenuItemResponse();
        res.setId(item.getId());
        res.setRestaurantId(item.getRestaurantId());
        res.setCategoryId(item.getCategoryId());
        res.setName(item.getName());
        res.setDescription(item.getDescription());
        res.setPrice(item.getPrice());
        res.setImageUrl(item.getImageUrl());
        res.setIsAvailable(item.getIsAvailable());
        res.setIsVegetarian(item.getIsVegetarian());
        res.setPreparationTimeMinutes(item.getPreparationTimeMinutes());
        res.setCreatedAt(item.getCreatedAt());
        res.setUpdatedAt(item.getUpdatedAt());
        return res;
    }
}
