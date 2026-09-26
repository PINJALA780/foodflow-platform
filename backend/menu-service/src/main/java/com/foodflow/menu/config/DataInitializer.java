package com.foodflow.menu.config;

import com.foodflow.menu.entity.MenuCategory;
import com.foodflow.menu.entity.MenuItem;
import com.foodflow.menu.repository.MenuCategoryRepository;
import com.foodflow.menu.repository.MenuItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    private final MenuCategoryRepository categoryRepository;
    private final MenuItemRepository itemRepository;

    public DataInitializer(MenuCategoryRepository categoryRepository, MenuItemRepository itemRepository) {
        this.categoryRepository = categoryRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public void run(String... args) {
        if (itemRepository.count() == 0) {
            log.info("Seeding initial sample menu items...");

            UUID defaultRestaurantId = UUID.fromString("00000000-0000-0000-0000-000000000001");

            MenuCategory mainCourse = new MenuCategory();
            mainCourse.setRestaurantId(defaultRestaurantId);
            mainCourse.setName("Main Course");
            mainCourse.setDescription("Hearty and fulfilling main dishes");
            mainCourse.setDisplayOrder(1);
            MenuCategory savedCat = categoryRepository.save(mainCourse);

            MenuItem i1 = createItem(defaultRestaurantId, savedCat.getId(), "Chicken Biryani", "Fragrant basmati rice cooked with tender chicken and spices", new BigDecimal("14.99"), false, 25);
            MenuItem i2 = createItem(defaultRestaurantId, savedCat.getId(), "Paneer Biryani", "Aromatic rice with spiced paneer cubes and herbs", new BigDecimal("12.99"), true, 20);
            MenuItem i3 = createItem(defaultRestaurantId, savedCat.getId(), "Butter Chicken", "Rich and creamy tomato-butter gravy with chicken", new BigDecimal("13.99"), false, 20);
            MenuItem i4 = createItem(defaultRestaurantId, savedCat.getId(), "Veg Fried Rice", "Wok-tossed rice with fresh seasonal vegetables", new BigDecimal("9.99"), true, 15);
            MenuItem i5 = createItem(defaultRestaurantId, savedCat.getId(), "Margherita Pizza", "Classic tomato sauce, fresh mozzarella, and basil", new BigDecimal("11.99"), true, 20);
            MenuItem i6 = createItem(defaultRestaurantId, savedCat.getId(), "Chicken Burger", "Crispy fried chicken patty with lettuce and spicy mayo", new BigDecimal("8.99"), false, 15);
            MenuItem i7 = createItem(defaultRestaurantId, savedCat.getId(), "Masala Dosa", "Crispy crepe filled with spiced potato mash", new BigDecimal("7.99"), true, 15);
            MenuItem i8 = createItem(defaultRestaurantId, savedCat.getId(), "Paneer Butter Masala", "Cottage cheese in rich creamy gravy", new BigDecimal("12.49"), true, 20);
            MenuItem i9 = createItem(defaultRestaurantId, savedCat.getId(), "Chicken Noodles", "Stir-fried noodles with chicken strips and veggies", new BigDecimal("10.99"), false, 15);

            itemRepository.saveAll(List.of(i1, i2, i3, i4, i5, i6, i7, i8, i9));
            log.info("Successfully seeded sample menu items.");
        }
    }

    private MenuItem createItem(UUID rId, UUID catId, String name, String desc, BigDecimal price, boolean isVeg, int prepTime) {
        MenuItem item = new MenuItem();
        item.setRestaurantId(rId);
        item.setCategoryId(catId);
        item.setName(name);
        item.setDescription(desc);
        item.setPrice(price);
        item.setImageUrl("https://images.unsplash.com/photo-1546069901-ba9599a7e63c");
        item.setIsAvailable(true);
        item.setIsVegetarian(isVeg);
        item.setPreparationTimeMinutes(prepTime);
        return item;
    }
}
