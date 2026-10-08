package com.example.foodordering.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.foodordering.dto.ResponseStructure;
import com.example.foodordering.dto.RestaurantRequest;
import com.example.foodordering.entity.MenuItem;
import com.example.foodordering.entity.Restaurant;
import com.example.foodordering.exception.BusinessException;
import com.example.foodordering.exception.IdNotFoundException;
import com.example.foodordering.exception.NoRecordAvailableException;
import com.example.foodordering.repository.MenuItemRepository;
import com.example.foodordering.repository.RestaurantRepository;

@Service
public class RestaurantService {

	@Autowired
	private RestaurantRepository restaurantRepository;

	@Autowired
	private MenuItemRepository menuItemRepository;

	// =========================================================
	// ADD RESTAURANT
	// =========================================================

	public ResponseStructure<Restaurant> addRestaurant(RestaurantRequest request) {

		if (restaurantRepository.findByNameIgnoreCase(request.getName()).isPresent()) {

			throw new BusinessException("Restaurant with name " + request.getName() + " already exists");
		}

		Restaurant restaurant = new Restaurant();

		restaurant.setName(request.getName());
		restaurant.setLocation(request.getLocation());
		restaurant.setRating(request.getRating());

		Restaurant savedRestaurant = restaurantRepository.save(restaurant);

		ResponseStructure<Restaurant> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.CREATED.value());
		res.setMessage("Restaurant Added Successfully");
		res.setData(savedRestaurant);

		return res;
	}

	// =========================================================
	// GET ALL RESTAURANTS
	// =========================================================

	public ResponseStructure<List<Restaurant>> getAllRestaurants() {

		List<Restaurant> restaurants = restaurantRepository.findAll();

		if (restaurants.isEmpty()) {

			throw new NoRecordAvailableException("No Restaurants Available");
		}

		ResponseStructure<List<Restaurant>> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.OK.value());
		res.setMessage("Restaurants Found");
		res.setData(restaurants);

		return res;
	}

	// =========================================================
	// GET RESTAURANT BY ID
	// =========================================================

	public ResponseStructure<Restaurant> getRestaurantById(Integer id) {

		Optional<Restaurant> opt = restaurantRepository.findById(id);

		if (opt.isEmpty()) {

			throw new IdNotFoundException("Restaurant Id " + id + " Not Found");
		}

		ResponseStructure<Restaurant> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.OK.value());
		res.setMessage("Restaurant Found");
		res.setData(opt.get());

		return res;
	}

	// =========================================================
	// UPDATE RESTAURANT
	// =========================================================

	public ResponseStructure<Restaurant> updateRestaurant(Integer id, RestaurantRequest request) {

		Optional<Restaurant> opt = restaurantRepository.findById(id);

		if (opt.isEmpty()) {

			throw new IdNotFoundException("Restaurant Id " + id + " Not Found");
		}

		Optional<Restaurant> existingRestaurant = restaurantRepository.findByNameIgnoreCase(request.getName());

		if (existingRestaurant.isPresent() && !existingRestaurant.get().getId().equals(id)) {

			throw new BusinessException("Restaurant with name " + request.getName() + " already exists");
		}

		Restaurant restaurant = opt.get();

		restaurant.setName(request.getName());
		restaurant.setLocation(request.getLocation());
		restaurant.setRating(request.getRating());

		Restaurant updatedRestaurant = restaurantRepository.save(restaurant);

		ResponseStructure<Restaurant> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.OK.value());
		res.setMessage("Restaurant Updated Successfully");
		res.setData(updatedRestaurant);

		return res;
	}

	// =========================================================
	// DELETE RESTAURANT
	// =========================================================

	@Transactional
	public ResponseStructure<String> deleteRestaurant(Integer id) {

		Optional<Restaurant> opt = restaurantRepository.findById(id);

		if (opt.isEmpty()) {

			throw new IdNotFoundException("Restaurant Id " + id + " Not Found");
		}

		List<MenuItem> menuItems = menuItemRepository.findByRestaurantId(id);

		if (!menuItems.isEmpty()) {

			throw new BusinessException("Restaurant cannot be deleted because " + "menu items are associated with it");
		}

		restaurantRepository.delete(opt.get());

		ResponseStructure<String> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.OK.value());
		res.setMessage("Restaurant Deleted Successfully");
		res.setData("Restaurant deleted successfully");

		return res;
	}

	// =========================================================
	// GET RESTAURANTS BY LOCATION
	// =========================================================

	public ResponseStructure<List<Restaurant>> getRestaurantsByLocation(String location) {

		List<Restaurant> restaurants = restaurantRepository.findByLocationIgnoreCase(location);

		if (restaurants.isEmpty()) {

			throw new NoRecordAvailableException("No Restaurants Found in Location " + location);
		}

		ResponseStructure<List<Restaurant>> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.OK.value());
		res.setMessage("Restaurants Found");
		res.setData(restaurants);

		return res;
	}

	// =========================================================
	// GET RESTAURANT BY NAME
	// =========================================================

	public ResponseStructure<Restaurant> getRestaurantByName(String name) {

		Optional<Restaurant> opt = restaurantRepository.findByNameIgnoreCase(name);

		if (opt.isEmpty()) {

			throw new IdNotFoundException("Restaurant with name " + name + " Not Found");
		}

		ResponseStructure<Restaurant> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.OK.value());
		res.setMessage("Restaurant Found");
		res.setData(opt.get());

		return res;
	}

	// =========================================================
	// GET RESTAURANTS BY RATING GREATER THAN VALUE
	// =========================================================

	public ResponseStructure<List<Restaurant>> getRestaurantsByRatingGreaterThan(Double rating) {

		if (rating < 0 || rating > 5) {

			throw new BusinessException("Rating must be between 0 and 5");
		}

		List<Restaurant> restaurants = restaurantRepository.findByRatingGreaterThan(rating);

		if (restaurants.isEmpty()) {

			throw new NoRecordAvailableException("No Restaurants Found with Rating Greater Than " + rating);
		}

		ResponseStructure<List<Restaurant>> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.OK.value());
		res.setMessage("Restaurants Found");
		res.setData(restaurants);

		return res;
	}

	// =========================================================
	// GET MENU OF A RESTAURANT
	// =========================================================

	public ResponseStructure<List<MenuItem>> getMenuOfRestaurant(Integer restaurantId) {

		Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);

		if (restaurant.isEmpty()) {

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
