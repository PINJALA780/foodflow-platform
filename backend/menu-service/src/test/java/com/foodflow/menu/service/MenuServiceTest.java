package com.foodflow.menu.service;

import com.foodflow.menu.dto.CreateMenuItemRequest;
import com.foodflow.menu.dto.MenuItemResponse;
import com.foodflow.menu.dto.UpdateMenuItemRequest;
import com.foodflow.menu.entity.MenuItem;
import com.foodflow.menu.exception.ResourceNotFoundException;
import com.foodflow.menu.repository.MenuCategoryRepository;
import com.foodflow.menu.repository.MenuItemRepository;
import com.foodflow.menu.service.impl.MenuServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuCategoryRepository categoryRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @InjectMocks
    private MenuServiceImpl menuService;

    private MenuItem sampleItem;
    private UUID sampleId;
    private UUID sampleRestaurantId;

    @BeforeEach
    void setUp() {
        sampleId = UUID.randomUUID();
        sampleRestaurantId = UUID.randomUUID();

        sampleItem = new MenuItem();
        sampleItem.setId(sampleId);
        sampleItem.setRestaurantId(sampleRestaurantId);
        sampleItem.setName("Chicken Biryani");
        sampleItem.setPrice(new BigDecimal("14.99"));
        sampleItem.setIsAvailable(true);
        sampleItem.setIsVegetarian(false);
    }

    @Test
    void createMenuItem_Success() {
        CreateMenuItemRequest req = new CreateMenuItemRequest();
        req.setRestaurantId(sampleRestaurantId);
        req.setName("Chicken Biryani");
        req.setPrice(new BigDecimal("14.99"));

        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(sampleItem);

        MenuItemResponse response = menuService.createMenuItem(req);

        assertNotNull(response);
        assertEquals("Chicken Biryani", response.getName());
        assertEquals(new BigDecimal("14.99"), response.getPrice());
    }

    @Test
    void getMenuItemById_Success() {
        when(menuItemRepository.findById(sampleId)).thenReturn(Optional.of(sampleItem));

        MenuItemResponse response = menuService.getMenuItemById(sampleId);

        assertNotNull(response);
        assertEquals(sampleId, response.getId());
    }

    @Test
    void getMenuItemById_NotFound() {
        UUID nonExistent = UUID.randomUUID();
        when(menuItemRepository.findById(nonExistent)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> menuService.getMenuItemById(nonExistent));
    }

    @Test
    void getMenuItemsByRestaurant_Success() {
        when(menuItemRepository.findByRestaurantId(sampleRestaurantId)).thenReturn(List.of(sampleItem));

        List<MenuItemResponse> results = menuService.getMenuItemsByRestaurant(sampleRestaurantId, false);

        assertEquals(1, results.size());
        assertEquals("Chicken Biryani", results.get(0).getName());
    }

    @Test
    void updateMenuItem_Success() {
        UpdateMenuItemRequest req = new UpdateMenuItemRequest();
        req.setName("Spicy Chicken Biryani");
        req.setPrice(new BigDecimal("15.99"));

        when(menuItemRepository.findById(sampleId)).thenReturn(Optional.of(sampleItem));
        when(menuItemRepository.save(any(MenuItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MenuItemResponse response = menuService.updateMenuItem(sampleId, req);

        assertNotNull(response);
        assertEquals("Spicy Chicken Biryani", response.getName());
    }

    @Test
    void deleteMenuItem_Success() {
        when(menuItemRepository.findById(sampleId)).thenReturn(Optional.of(sampleItem));
        doNothing().when(menuItemRepository).delete(sampleItem);

        assertDoesNotThrow(() -> menuService.deleteMenuItem(sampleId));
        verify(menuItemRepository, times(1)).delete(sampleItem);
    }
}
