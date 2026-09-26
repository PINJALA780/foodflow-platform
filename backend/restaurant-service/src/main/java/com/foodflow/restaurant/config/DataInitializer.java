package com.foodflow.restaurant.config;

import com.foodflow.restaurant.entity.Restaurant;
import com.foodflow.restaurant.repository.RestaurantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    private final RestaurantRepository restaurantRepository;

    public DataInitializer(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public void run(String... args) {
        if (restaurantRepository.count() == 0) {
            log.info("Seeding initial sample restaurant data...");

            Restaurant r1 = new Restaurant();
            r1.setName("Spice Garden");
            r1.setDescription("Authentic Indian curry & tandoori delights");
            r1.setCategory("Indian");
            r1.setAddress("123 Jubilee Hills");
            r1.setCity("Hyderabad");
            r1.setPhone("+91-9876543210");
            r1.setImageUrl("https://images.unsplash.com/photo-1517248135467-4c7edcad34c4");
            r1.setRating(new BigDecimal("4.5"));
            r1.setDeliveryTimeMinutes(30);
            r1.setIsOpen(true);

            Restaurant r2 = new Restaurant();
            r2.setName("Urban Pizza");
            r2.setDescription("Woodfired authentic sourdough pizzas & pasta");
            r2.setCategory("Pizza");
            r2.setAddress("45 Hitech City");
            r2.setCity("Hyderabad");
            r2.setPhone("+91-9876543211");
            r2.setImageUrl("https://images.unsplash.com/photo-1513104890138-7c749659a591");
            r2.setRating(new BigDecimal("4.7"));
            r2.setDeliveryTimeMinutes(25);
            r2.setIsOpen(true);

            Restaurant r3 = new Restaurant();
            r3.setName("Burger House");
            r3.setDescription("Juicy gourmet burgers & loaded crispy fries");
            r3.setCategory("Fast Food");
            r3.setAddress("78 Banjara Hills");
            r3.setCity("Hyderabad");
            r3.setPhone("+91-9876543212");
            r3.setImageUrl("https://images.unsplash.com/photo-1568901346375-23c9450c58cd");
            r3.setRating(new BigDecimal("4.3"));
            r3.setDeliveryTimeMinutes(20);
            r3.setIsOpen(true);

            Restaurant r4 = new Restaurant();
            r4.setName("Hyderabad Biryani Hub");
            r4.setDescription("Legendary Hyderabadi Dum Biryani & Kebabs");
            r4.setCategory("Biryani");
            r4.setAddress("12 Madhapur Main Rd");
            r4.setCity("Hyderabad");
            r4.setPhone("+91-9876543213");
            r4.setImageUrl("https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8");
            r4.setRating(new BigDecimal("4.8"));
            r4.setDeliveryTimeMinutes(35);
            r4.setIsOpen(true);

            Restaurant r5 = new Restaurant();
            r5.setName("South Indian Kitchen");
            r5.setDescription("Crispy dosas, fluffy idlis & hot filter coffee");
            r5.setCategory("South Indian");
            r5.setAddress("99 Gachibowli");
            r5.setCity("Hyderabad");
            r5.setPhone("+91-9876543214");
            r5.setImageUrl("https://images.unsplash.com/photo-1589301760014-d929f3979dbc");
            r5.setRating(new BigDecimal("4.6"));
            r5.setDeliveryTimeMinutes(25);
            r5.setIsOpen(true);

            restaurantRepository.saveAll(List.of(r1, r2, r3, r4, r5));
            log.info("Successfully seeded 5 sample restaurants.");
        }
    }
}
