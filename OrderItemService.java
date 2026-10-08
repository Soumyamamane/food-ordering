package com.example.foodordering.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.foodordering.dto.AddOrderItemRequest;
import com.example.foodordering.dto.ResponseStructure;
import com.example.foodordering.entity.MenuItem;
import com.example.foodordering.entity.OrderItem;
import com.example.foodordering.entity.OrderStatus;
import com.example.foodordering.entity.Orders;
import com.example.foodordering.exception.BusinessException;
import com.example.foodordering.exception.IdNotFoundException;
import com.example.foodordering.exception.NoRecordAvailableException;
import com.example.foodordering.repository.MenuItemRepository;
import com.example.foodordering.repository.OrderItemRepository;
import com.example.foodordering.repository.OrderRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderItemService {

	@Autowired
	private OrderItemRepository orderItemRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private MenuItemRepository menuItemRepository;

	// =========================================================
	// ADD ITEM TO EXISTING ORDER
	// =========================================================

	@Transactional
	public ResponseStructure<OrderItem> addItemToOrder(Integer orderId, AddOrderItemRequest request) {

		Orders order = getOrder(orderId);

		// Order must still be editable

		if (order.getStatus() == OrderStatus.CANCELLED) {

			throw new BusinessException("Cancelled order cannot be modified");
		}

		if (order.getStatus() == OrderStatus.PREPARING || order.getStatus() == OrderStatus.READY
				|| order.getStatus() == OrderStatus.OUT_FOR_DELIVERY || order.getStatus() == OrderStatus.DELIVERED) {

			throw new BusinessException("Items cannot be added after order preparation has started");
		}

		// Find menu item

		Optional<MenuItem> menuItemOpt = menuItemRepository.findById(request.getMenuItemId());

		if (menuItemOpt.isEmpty()) {

			throw new IdNotFoundException("Menu Item Id " + request.getMenuItemId() + " Not Found");
		}

		MenuItem menuItem = menuItemOpt.get();

		// Check availability

		if (!menuItem.getAvailability()) {

			throw new BusinessException("Menu item '" + menuItem.getItemName() + "' is unavailable");
		}

		// Check restaurant

		if (!menuItem.getRestaurant().getId().equals(order.getRestaurant().getId())) {

			throw new BusinessException("Menu item does not belong to this restaurant");
		}

		// Calculate subtotal

		BigDecimal subtotal = menuItem.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

		// Create OrderItem

		OrderItem orderItem = new OrderItem();

		orderItem.setOrder(order);
		orderItem.setMenuItem(menuItem);
		orderItem.setQuantity(request.getQuantity());
		orderItem.setSubTotal(subtotal);

		OrderItem savedItem = orderItemRepository.save(orderItem);

		// Recalculate order total

		recalculateOrderTotal(order);

		ResponseStructure<OrderItem> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.CREATED.value());

		res.setMessage("Order Item Added Successfully");

		res.setData(savedItem);

		return res;
	}

	// =========================================================
	// UPDATE ITEM QUANTITY
	// =========================================================

	@Transactional
	public ResponseStructure<OrderItem> updateItemQuantity(Integer orderItemId, Integer quantity) {

		if (quantity == null || quantity < 1) {

			throw new BusinessException("Quantity must be at least 1");
		}

		Optional<OrderItem> opt = orderItemRepository.findById(orderItemId);

		if (opt.isEmpty()) {

			throw new IdNotFoundException("Order Item Id " + orderItemId + " Not Found");
		}

		OrderItem orderItem = opt.get();

		Orders order = orderItem.getOrder();

		// Check order modification

		validateOrderCanBeModified(order);

		// Calculate new subtotal

		BigDecimal subtotal = orderItem.getMenuItem().getPrice().multiply(BigDecimal.valueOf(quantity));

		orderItem.setQuantity(quantity);
		orderItem.setSubTotal(subtotal);

		OrderItem savedItem = orderItemRepository.save(orderItem);

		// Recalculate total

		recalculateOrderTotal(order);

		ResponseStructure<OrderItem> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.OK.value());

		res.setMessage("Order Item Quantity Updated Successfully");

		res.setData(savedItem);

		return res;
	}

	// =========================================================
	// REMOVE ITEM FROM ORDER
	// =========================================================

	@Transactional
	public ResponseStructure<String> removeItemFromOrder(Integer orderItemId) {

		Optional<OrderItem> opt = orderItemRepository.findById(orderItemId);

		if (opt.isEmpty()) {

			throw new IdNotFoundException("Order Item Id " + orderItemId + " Not Found");
		}

		OrderItem orderItem = opt.get();

		Orders order = orderItem.getOrder();

		// Check modification

		validateOrderCanBeModified(order);

		// Order must contain at least one item

		List<OrderItem> items = orderItemRepository.findByOrderOrderId(order.getOrderId());

		if (items.size() <= 1) {

			throw new BusinessException("Order must contain at least one order item");
		}

		// Remove item

		orderItemRepository.delete(orderItem);

		items.remove(orderItem);

		// Recalculate total

		BigDecimal total = BigDecimal.ZERO;

		for (OrderItem item : items) {

			total = total.add(item.getSubTotal());
		}

		order.setTotalAmount(total);

		// Update payment amount

		if (order.getPayment() != null) {

			order.getPayment().setPaymentAmount(total);
		}

		orderRepository.save(order);

		ResponseStructure<String> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.OK.value());

		res.setMessage("Order Item Removed Successfully");

		res.setData("Order item removed successfully");

		return res;
	}

	// =========================================================
	// GET ALL ORDER ITEMS OF AN ORDER
	// =========================================================

	public ResponseStructure<List<OrderItem>> getAllOrderItems(Integer orderId) {

		getOrder(orderId);

		List<OrderItem> items = orderItemRepository.findByOrderOrderId(orderId);

		if (items.isEmpty()) {

			throw new NoRecordAvailableException("No Order Items Found for Order Id " + orderId);
		}

		ResponseStructure<List<OrderItem>> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.OK.value());

		res.setMessage("Order Items Found");

		res.setData(items);

		return res;
	}

	// =========================================================
	// GET ORDER
	// =========================================================

	private Orders getOrder(Integer orderId) {

		Optional<Orders> opt = orderRepository.findById(orderId);

		if (opt.isEmpty()) {

			throw new IdNotFoundException("Order Id " + orderId + " Not Found");
		}

		return opt.get();
	}

	// =========================================================
	// VALIDATE ORDER MODIFICATION
	// =========================================================

	private void validateOrderCanBeModified(Orders order) {

		if (order.getStatus() == OrderStatus.CANCELLED) {

			throw new BusinessException("Cancelled order cannot be modified");
		}

		if (order.getStatus() == OrderStatus.PREPARING || order.getStatus() == OrderStatus.READY
				|| order.getStatus() == OrderStatus.OUT_FOR_DELIVERY || order.getStatus() == OrderStatus.DELIVERED) {

			throw new BusinessException("Order cannot be modified after preparation has started");
		}
	}

	// =========================================================
	// RECALCULATE ORDER TOTAL
	// =========================================================

	private void recalculateOrderTotal(Orders order) {

		List<OrderItem> items = orderItemRepository.findByOrderOrderId(order.getOrderId());

		BigDecimal total = BigDecimal.ZERO;

		for (OrderItem item : items) {

			total = total.add(item.getSubTotal());
		}

		order.setTotalAmount(total);

		// Payment amount must equal order total

		if (order.getPayment() != null) {

			order.getPayment().setPaymentAmount(total);
		}

		orderRepository.save(order);
	}
}
