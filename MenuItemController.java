package com.example.foodordering.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.foodordering.dto.MenuItemRequest;
import com.example.foodordering.dto.ResponseStructure;
import com.example.foodordering.dto.UpdateMenuItemRequest;
import com.example.foodordering.entity.MenuItem;
import com.example.foodordering.service.MenuItemService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/menu-items")
public class MenuItemController {

	@Autowired
	private MenuItemService menuItemService;

	// =========================================================
	// ADD ITEM TO RESTAURANT
	// =========================================================

	@PostMapping("/restaurant/{restaurantId}")
	public ResponseEntity<ResponseStructure<MenuItem>> addMenuItem(@PathVariable Integer restaurantId,
			@Valid @RequestBody MenuItemRequest request) {

		request.setRestaurantId(restaurantId);

		return new ResponseEntity<>(menuItemService.addMenuItem(request), HttpStatus.CREATED);
	}

	// =========================================================
	// GET ALL ITEMS
	// =========================================================

	@GetMapping
	public ResponseEntity<ResponseStructure<List<MenuItem>>> getAllItems() {

		return new ResponseEntity<>(menuItemService.getAllItems(), HttpStatus.OK);
	}

	// =========================================================
	// GET ITEM BY ID
	// =========================================================

	@GetMapping("/{id}")
	public ResponseEntity<ResponseStructure<MenuItem>> getItemById(@PathVariable Integer id) {

		return new ResponseEntity<>(menuItemService.getItemById(id), HttpStatus.OK);
	}

	// =========================================================
	// UPDATE PRICE AND AVAILABILITY
	// =========================================================

	@PutMapping("/{id}")
	public ResponseEntity<ResponseStructure<MenuItem>> updatePriceAndAvailability(@PathVariable Integer id,
			@Valid @RequestBody UpdateMenuItemRequest request) {

		return new ResponseEntity<>(menuItemService.updatePriceAndAvailability(id, request), HttpStatus.OK);
	}

	// =========================================================
	// SORT BY PRICE
	// =========================================================

	@GetMapping("/sort-by-price")
	public ResponseEntity<ResponseStructure<List<MenuItem>>> sortByPrice() {

		return new ResponseEntity<>(menuItemService.sortByPrice(), HttpStatus.OK);
	}

	// =========================================================
	// GET ITEMS BY NAME
	// =========================================================

	@GetMapping("/name/{name}")
	public ResponseEntity<ResponseStructure<List<MenuItem>>> getItemsByName(@PathVariable String name) {

		return new ResponseEntity<>(menuItemService.getItemsByName(name), HttpStatus.OK);
	}

	// =========================================================
	// GET ALL ITEMS IN RESTAURANT
	// =========================================================

	@GetMapping("/restaurant/{restaurantId}")
	public ResponseEntity<ResponseStructure<List<MenuItem>>> getAllItemsInRestaurant(
			@PathVariable Integer restaurantId) {

		return new ResponseEntity<>(menuItemService.getAllItemsInRestaurant(restaurantId), HttpStatus.OK);
	}
}
