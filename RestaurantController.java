package com.example.foodordering.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.foodordering.dto.ResponseStructure;
import com.example.foodordering.dto.RestaurantRequest;
import com.example.foodordering.entity.MenuItem;
import com.example.foodordering.entity.Restaurant;
import com.example.foodordering.service.RestaurantService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

	@Autowired
	private RestaurantService restaurantService;

	// =========================================================
	// ADD RESTAURANT
	// =========================================================

	@PostMapping
	public ResponseEntity<ResponseStructure<Restaurant>> addRestaurant(@Valid @RequestBody RestaurantRequest request) {

		return new ResponseEntity<>(restaurantService.addRestaurant(request), HttpStatus.CREATED);
	}

	// =========================================================
	// GET ALL RESTAURANTS
	// =========================================================

	@GetMapping
	public ResponseEntity<ResponseStructure<List<Restaurant>>> getAllRestaurants() {

		return new ResponseEntity<>(restaurantService.getAllRestaurants(), HttpStatus.OK);
	}

	// =========================================================
	// GET RESTAURANT BY ID
	// =========================================================

	@GetMapping("/{id}")
	public ResponseEntity<ResponseStructure<Restaurant>> getRestaurantById(@PathVariable Integer id) {

		return new ResponseEntity<>(restaurantService.getRestaurantById(id), HttpStatus.OK);
	}

	// =========================================================
	// UPDATE RESTAURANT
	// =========================================================

	@PutMapping("/{id}")
	public ResponseEntity<ResponseStructure<Restaurant>> updateRestaurant(@PathVariable Integer id,
			@Valid @RequestBody RestaurantRequest request) {

		return new ResponseEntity<>(restaurantService.updateRestaurant(id, request), HttpStatus.OK);
	}

	// =========================================================
	// DELETE RESTAURANT
	// =========================================================

	@DeleteMapping("/{id}")
	public ResponseEntity<ResponseStructure<String>> deleteRestaurant(@PathVariable Integer id) {

		return new ResponseEntity<>(restaurantService.deleteRestaurant(id), HttpStatus.OK);
	}

	// =========================================================
	// GET RESTAURANTS BY LOCATION
	// =========================================================

	@GetMapping("/location/{location}")
	public ResponseEntity<ResponseStructure<List<Restaurant>>> getRestaurantsByLocation(@PathVariable String location) {

		return new ResponseEntity<>(restaurantService.getRestaurantsByLocation(location), HttpStatus.OK);
	}

	// =========================================================
	// GET RESTAURANT BY NAME
	// =========================================================

	@GetMapping("/name/{name}")
	public ResponseEntity<ResponseStructure<Restaurant>> getRestaurantByName(@PathVariable String name) {

		return new ResponseEntity<>(restaurantService.getRestaurantByName(name), HttpStatus.OK);
	}

	// =========================================================
	// GET RESTAURANTS BY RATING
	// =========================================================

	@GetMapping("/rating/greater-than/{rating}")
	public ResponseEntity<ResponseStructure<List<Restaurant>>> getRestaurantsByRatingGreaterThan(
			@PathVariable Double rating) {

		return new ResponseEntity<>(restaurantService.getRestaurantsByRatingGreaterThan(rating), HttpStatus.OK);
	}

	// =========================================================
	// GET MENU OF RESTAURANT
	// =========================================================

	@GetMapping("/{restaurantId}/menu")
	public ResponseEntity<ResponseStructure<List<MenuItem>>> getMenuOfRestaurant(@PathVariable Integer restaurantId) {

		return new ResponseEntity<>(restaurantService.getMenuOfRestaurant(restaurantId), HttpStatus.OK);
	}
}
