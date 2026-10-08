package com.example.foodordering.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.foodordering.entity.MenuItem;

public interface MenuItemRepository extends JpaRepository<MenuItem, Integer> {

    List<MenuItem> findByItemNameContainingIgnoreCase(String itemName);

    List<MenuItem> findByRestaurantId(Integer restaurantId);

    List<MenuItem> findAllByOrderByPriceAsc();

    List<MenuItem> findAllByOrderByPriceDesc();
}