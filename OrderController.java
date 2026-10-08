package com.example.foodordering.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.foodordering.dto.PlaceOrderRequest;
import com.example.foodordering.dto.ResponseStructure;
import com.example.foodordering.entity.OrderStatus;
import com.example.foodordering.entity.Orders;
import com.example.foodordering.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	@Autowired
	private OrderService orderService;

	// =========================================================
	// GET ALL ORDERS
	// =========================================================

	@GetMapping
	public ResponseEntity<ResponseStructure<List<Orders>>> getAllOrders() {

		return new ResponseEntity<>(orderService.getAllOrders(), HttpStatus.OK);
	}

	// =========================================================
	// GET ORDER BY ID
	// =========================================================

	@GetMapping("/{orderId}")
	public ResponseEntity<ResponseStructure<Orders>> getOrderById(@PathVariable Integer orderId) {

		return new ResponseEntity<>(orderService.getOrderById(orderId), HttpStatus.OK);
	}

	// =========================================================
	// GET ALL ORDERS OF CUSTOMER
	// =========================================================

	@GetMapping("/customer/{customerId}")
	public ResponseEntity<ResponseStructure<List<Orders>>> getAllOrdersOfCustomer(@PathVariable Integer customerId) {

		return new ResponseEntity<>(orderService.getAllOrdersOfCustomer(customerId), HttpStatus.OK);
	}

	// =========================================================
	// UPDATE ORDER STATUS
	// =========================================================

	@PutMapping("/{orderId}/status")
	public ResponseEntity<ResponseStructure<Orders>> updateOrderStatus(@PathVariable Integer orderId,
			@RequestParam OrderStatus status) {

		return new ResponseEntity<>(orderService.updateOrderStatus(orderId, status), HttpStatus.OK);
	}

	// =========================================================
	// CANCEL ORDER
	// =========================================================

	@PutMapping("/{orderId}/cancel")
	public ResponseEntity<ResponseStructure<Orders>> cancelOrder(@PathVariable Integer orderId) {

		return new ResponseEntity<>(orderService.cancelOrder(orderId), HttpStatus.OK);
	}

	// =========================================================
	// GET ORDERS BY STATUS
	// =========================================================

	@GetMapping("/status/{status}")
	public ResponseEntity<ResponseStructure<List<Orders>>> getOrdersByStatus(@PathVariable OrderStatus status) {

		return new ResponseEntity<>(orderService.getOrdersByStatus(status), HttpStatus.OK);
	}

	// =========================================================
	// GET ORDERS BY DATE
	// =========================================================

	@GetMapping("/date/{date}")
	public ResponseEntity<ResponseStructure<List<Orders>>> getOrdersByDate(@PathVariable LocalDate date) {

		return new ResponseEntity<>(orderService.getOrdersByDate(date), HttpStatus.OK);
	}

	// =========================================================
	// GET ALL ORDERS OF RESTAURANT
	// =========================================================

	@GetMapping("/restaurant/{restaurantId}")
	public ResponseEntity<ResponseStructure<List<Orders>>> getAllOrdersOfRestaurant(
			@PathVariable Integer restaurantId) {

		return new ResponseEntity<>(orderService.getAllOrdersOfRestaurant(restaurantId), HttpStatus.OK);
	}

	// =========================================================
	// PLACE ORDER
	// =========================================================

	@PostMapping
	public ResponseEntity<ResponseStructure<Orders>> placeOrder(@Valid @RequestBody PlaceOrderRequest request) {

		return new ResponseEntity<>(orderService.placeOrder(request), HttpStatus.CREATED);
	}
}
