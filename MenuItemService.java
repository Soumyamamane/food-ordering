
package com.example.foodordering.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.foodordering.dto.MenuItemRequest;
import com.example.foodordering.dto.ResponseStructure;
import com.example.foodordering.dto.UpdateMenuItemRequest;
import com.example.foodordering.entity.MenuItem;
import com.example.foodordering.entity.Restaurant;
import com.example.foodordering.exception.IdNotFoundException;
import com.example.foodordering.exception.NoRecordAvailableException;
import com.example.foodordering.repository.MenuItemRepository;
import com.example.foodordering.repository.RestaurantRepository;

@Service
public class MenuItemService {

	@Autowired
	private MenuItemRepository menuItemRepository;

	@Autowired
	private RestaurantRepository restaurantRepository;

	// =========================================================
	// ADD MENU ITEM TO RESTAURANT
	// =========================================================

	public ResponseStructure<MenuItem> addMenuItem(MenuItemRequest request) {

		Optional<Restaurant> restaurantOpt = restaurantRepository.findById(request.getRestaurantId());

		if (restaurantOpt.isEmpty()) {

			throw new IdNotFoundException("Restaurant Id " + request.getRestaurantId() + " Not Found");
		}

		Restaurant restaurant = restaurantOpt.get();

		MenuItem menuItem = new MenuItem();

		menuItem.setItemName(request.getItemName());
		menuItem.setPrice(request.getPrice());
		menuItem.setAvailability(request.getAvailability());

		// Establish relationship
		menuItem.setRestaurant(restaurant);

		MenuItem savedMenuItem = menuItemRepository.save(menuItem);

		ResponseStructure<MenuItem> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.CREATED.value());
		res.setMessage("Menu Item Added Successfully");
		res.setData(savedMenuItem);

		return res;
	}

	// =========================================================
	// GET ALL ITEMS
	// =========================================================

	public ResponseStructure<List<MenuItem>> getAllItems() {

		List<MenuItem> menuItems = menuItemRepository.findAll();

		if (menuItems.isEmpty()) {

			throw new NoRecordAvailableException("No Menu Items Available");
		}

		ResponseStructure<List<MenuItem>> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.OK.value());
		res.setMessage("Menu Items Found");
		res.setData(menuItems);

		return res;
	}

	// =========================================================
	// GET ITEM BY ID
	// =========================================================

	public ResponseStructure<MenuItem> getItemById(Integer id) {

		Optional<MenuItem> opt = menuItemRepository.findById(id);

		if (opt.isEmpty()) {

			throw new IdNotFoundException("Menu Item Id " + id + " Not Found");
		}

		ResponseStructure<MenuItem> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.OK.value());
		res.setMessage("Menu Item Found");
		res.setData(opt.get());

		return res;
	}

	// =========================================================
	// UPDATE PRICE AND AVAILABILITY
	// =========================================================

	public ResponseStructure<MenuItem> updatePriceAndAvailability(Integer id, UpdateMenuItemRequest request) {

		Optional<MenuItem> opt = menuItemRepository.findById(id);

		if (opt.isEmpty()) {

			throw new IdNotFoundException("Menu Item Id " + id + " Not Found");
		}

		MenuItem menuItem = opt.get();

		menuItem.setPrice(request.getPrice());
		menuItem.setAvailability(request.getAvailability());

		MenuItem updatedMenuItem = menuItemRepository.save(menuItem);

		ResponseStructure<MenuItem> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.OK.value());
		res.setMessage("Menu Item Updated Successfully");
		res.setData(updatedMenuItem);

		return res;
	}

	// =========================================================
	// SORT BY PRICE ASCENDING
	// =========================================================

	public ResponseStructure<List<MenuItem>> sortByPrice() {

		List<MenuItem> menuItems = menuItemRepository.findAllByOrderByPriceAsc();

		if (menuItems.isEmpty()) {

			throw new NoRecordAvailableException("No Menu Items Available");
		}

		ResponseStructure<List<MenuItem>> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.OK.value());
		res.setMessage("Menu Items Sorted By Price");
		res.setData(menuItems);

		return res;
	}

	// =========================================================
	// GET ITEMS BY NAME
	// =========================================================

	public ResponseStructure<List<MenuItem>> getItemsByName(String name) {

		List<MenuItem> menuItems = menuItemRepository.findByItemNameContainingIgnoreCase(name);

		if (menuItems.isEmpty()) {

			throw new NoRecordAvailableException("No Menu Items Found with Name " + name);
		}

		ResponseStructure<List<MenuItem>> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.OK.value());
		res.setMessage("Menu Items Found");
		res.setData(menuItems);

		return res;
	}

	// =========================================================
	// GET ALL ITEMS IN A RESTAURANT
	// =========================================================

	public ResponseStructure<List<MenuItem>> getAllItemsInRestaurant(Integer restaurantId) {

		Optional<Restaurant> restaurantOpt = restaurantRepository.findById(restaurantId);

		if (restaurantOpt.isEmpty()) {

			throw new IdNotFoundException("Restaurant Id " + restaurantId + " Not Found");
		}

		List<MenuItem> menuItems = menuItemRepository.findByRestaurantId(restaurantId);

		if (menuItems.isEmpty()) {

			throw new NoRecordAvailableException("No Menu Items Available for Restaurant Id " + restaurantId);
		}

		ResponseStructure<List<MenuItem>> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.OK.value());
		res.setMessage("Menu Items Found");
		res.setData(menuItems);

		return res;
	}
}
