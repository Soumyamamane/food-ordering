package com.example.foodordering.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.foodordering.dto.AddOrderItemRequest;
import com.example.foodordering.dto.ResponseStructure;
import com.example.foodordering.entity.OrderItem;
import com.example.foodordering.service.OrderItemService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {

	@Autowired
	private OrderItemService orderItemService;

	// =========================================================
	// ADD ITEM TO EXISTING ORDER
	// =========================================================

	@PostMapping("/order/{orderId}")
	public ResponseEntity<ResponseStructure<OrderItem>> addItemToOrder(@PathVariable Integer orderId,
			@Valid @RequestBody AddOrderItemRequest request) {

		return new ResponseEntity<>(orderItemService.addItemToOrder(orderId, request), HttpStatus.CREATED);
	}

	// =========================================================
	// UPDATE ITEM QUANTITY
	// =========================================================

	@PutMapping("/{orderItemId}/quantity")
	public ResponseEntity<ResponseStructure<OrderItem>> updateItemQuantity(@PathVariable Integer orderItemId,
			@RequestParam Integer quantity) {

		return new ResponseEntity<>(orderItemService.updateItemQuantity(orderItemId, quantity), HttpStatus.OK);
	}

	// =========================================================
	// REMOVE ITEM FROM ORDER
	// =========================================================

	@DeleteMapping("/{orderItemId}")
	public ResponseEntity<ResponseStructure<String>> removeItemFromOrder(@PathVariable Integer orderItemId) {

		return new ResponseEntity<>(orderItemService.removeItemFromOrder(orderItemId), HttpStatus.OK);
	}

	// =========================================================
	// GET ALL ORDER ITEMS OF ORDER
	// =========================================================

	@GetMapping("/order/{orderId}")
	public ResponseEntity<ResponseStructure<List<OrderItem>>> getAllOrderItems(@PathVariable Integer orderId) {

		return new ResponseEntity<>(orderItemService.getAllOrderItems(orderId), HttpStatus.OK);
	}
}
