package com.example.foodordering.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.foodordering.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {

    List<Restaurant> findByLocationIgnoreCase(String location);

    Optional<Restaurant> findByNameIgnoreCase(String name);

    List<Restaurant> findByRatingGreaterThan(Double rating);
}