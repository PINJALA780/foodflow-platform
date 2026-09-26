package com.foodflow.restaurant.service;

import com.foodflow.restaurant.dto.CreateRestaurantRequest;
import com.foodflow.restaurant.dto.RestaurantResponse;
import com.foodflow.restaurant.dto.UpdateRestaurantRequest;
import com.foodflow.restaurant.entity.Restaurant;
import com.foodflow.restaurant.exception.RestaurantNotFoundException;
import com.foodflow.restaurant.repository.RestaurantRepository;
import com.foodflow.restaurant.service.impl.RestaurantServiceImpl;
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
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantServiceImpl restaurantService;

    private Restaurant sampleRestaurant;
    private UUID sampleId;

    @BeforeEach
    void setUp() {
        sampleId = UUID.randomUUID();
        sampleRestaurant = new Restaurant();
        sampleRestaurant.setId(sampleId);
        sampleRestaurant.setName("Spice Garden");
        sampleRestaurant.setCategory("Indian");
        sampleRestaurant.setCity("Hyderabad");
        sampleRestaurant.setAddress("123 Jubilee Hills");
        sampleRestaurant.setIsOpen(true);
        sampleRestaurant.setRating(new BigDecimal("4.5"));
    }

    @Test
    void createRestaurant_Success() {
        CreateRestaurantRequest req = new CreateRestaurantRequest();
        req.setName("Spice Garden");
        req.setCategory("Indian");
        req.setCity("Hyderabad");
        req.setAddress("123 Jubilee Hills");

        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(sampleRestaurant);

        RestaurantResponse response = restaurantService.createRestaurant(req);

        assertNotNull(response);
        assertEquals("Spice Garden", response.getName());
        verify(restaurantRepository, times(1)).save(any(Restaurant.class));
    }

    @Test
    void getRestaurantById_Success() {
        when(restaurantRepository.findById(sampleId)).thenReturn(Optional.of(sampleRestaurant));

        RestaurantResponse response = restaurantService.getRestaurantById(sampleId);

        assertNotNull(response);
        assertEquals(sampleId, response.getId());
        assertEquals("Spice Garden", response.getName());
    }

    @Test
    void getRestaurantById_NotFound() {
        UUID nonExistentId = UUID.randomUUID();
        when(restaurantRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(RestaurantNotFoundException.class, () -> restaurantService.getRestaurantById(nonExistentId));
    }

    @Test
    void getRestaurantsByCity_Success() {
        when(restaurantRepository.findByCityIgnoreCase("Hyderabad")).thenReturn(List.of(sampleRestaurant));

        List<RestaurantResponse> result = restaurantService.getRestaurantsByCity("Hyderabad");

        assertEquals(1, result.size());
        assertEquals("Hyderabad", result.get(0).getCity());
    }

    @Test
    void updateRestaurant_Success() {
        UpdateRestaurantRequest req = new UpdateRestaurantRequest();
        req.setName("Spice Garden Updated");
        req.setCategory("Indian");
        req.setAddress("456 Gachibowli");
        req.setCity("Hyderabad");

        when(restaurantRepository.findById(sampleId)).thenReturn(Optional.of(sampleRestaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RestaurantResponse response = restaurantService.updateRestaurant(sampleId, req);

        assertNotNull(response);
        assertEquals("Spice Garden Updated", response.getName());
    }

    @Test
    void deleteRestaurant_Success() {
        when(restaurantRepository.findById(sampleId)).thenReturn(Optional.of(sampleRestaurant));
        doNothing().when(restaurantRepository).delete(sampleRestaurant);

        assertDoesNotThrow(() -> restaurantService.deleteRestaurant(sampleId));
        verify(restaurantRepository, times(1)).delete(sampleRestaurant);
    }
}
