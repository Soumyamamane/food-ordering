package com.example.foodordering.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.foodordering.dto.OrderItemRequest;
import com.example.foodordering.dto.PlaceOrderRequest;
import com.example.foodordering.dto.ResponseStructure;
import com.example.foodordering.entity.Customer;
import com.example.foodordering.entity.MenuItem;
import com.example.foodordering.entity.OrderItem;
import com.example.foodordering.entity.OrderStatus;
import com.example.foodordering.entity.Orders;
import com.example.foodordering.entity.Payment;
import com.example.foodordering.entity.PaymentStatus;
import com.example.foodordering.entity.Restaurant;
import com.example.foodordering.exception.BusinessException;
import com.example.foodordering.exception.IdNotFoundException;
import com.example.foodordering.exception.NoRecordAvailableException;
import com.example.foodordering.repository.CustomerRepository;
import com.example.foodordering.repository.MenuItemRepository;
import com.example.foodordering.repository.OrderRepository;
import com.example.foodordering.repository.RestaurantRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private RestaurantRepository restaurantRepository;

	@Autowired
	private MenuItemRepository menuItemRepository;

	// =========================================================
	// PLACE ORDER
	// =========================================================

	@Transactional
	public ResponseStructure<Orders> placeOrder(PlaceOrderRequest request) {

		// 1. Find customer

		Optional<Customer> customerOpt = customerRepository.findById(request.getCustomerId());

		if (customerOpt.isEmpty()) {

			throw new IdNotFoundException("Customer Id " + request.getCustomerId() + " Not Found");
		}

		Customer customer = customerOpt.get();

		// 2. Find restaurant

		Optional<Restaurant> restaurantOpt = restaurantRepository.findById(request.getRestaurantId());

		if (restaurantOpt.isEmpty()) {

			throw new IdNotFoundException("Restaurant Id " + request.getRestaurantId() + " Not Found");
		}

		Restaurant restaurant = restaurantOpt.get();

		// 3. Create order

		Orders order = new Orders();

		order.setCustomer(customer);
		order.setRestaurant(restaurant);
		order.setOrderDateTime(LocalDateTime.now());
		order.setStatus(OrderStatus.PLACED);
		order.setTotalAmount(BigDecimal.ZERO);

		List<OrderItem> orderItems = new ArrayList<>();

		BigDecimal totalAmount = BigDecimal.ZERO;

		// 4. Process every order item

		for (OrderItemRequest itemRequest : request.getOrderItems()) {

			Optional<MenuItem> menuItemOpt = menuItemRepository.findById(itemRequest.getMenuItemId());

			if (menuItemOpt.isEmpty()) {

				throw new IdNotFoundException("Menu Item Id " + itemRequest.getMenuItemId() + " Not Found");
			}

			MenuItem menuItem = menuItemOpt.get();

			// 5. Check availability

			if (!menuItem.getAvailability()) {

				throw new BusinessException("Menu item '" + menuItem.getItemName() + "' is currently unavailable");
			}

			// 6. Check restaurant

			if (!menuItem.getRestaurant().getId().equals(request.getRestaurantId())) {

				throw new BusinessException(
						"Menu item '" + menuItem.getItemName() + "' does not belong to the selected restaurant");
			}

			// 7. Calculate subtotal

			BigDecimal subtotal = menuItem.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

			// 8. Create OrderItem

			OrderItem orderItem = new OrderItem();

			orderItem.setQuantity(itemRequest.getQuantity());

			orderItem.setSubTotal(subtotal);
			orderItem.setOrder(order);
			orderItem.setMenuItem(menuItem);

			orderItems.add(orderItem);

			// 9. Add subtotal to total

			totalAmount = totalAmount.add(subtotal);
		}

		// 10. Set order items

		order.setOrderItems(orderItems);

		// 11. Set final total

		order.setTotalAmount(totalAmount);

		// 12. Create payment

		Payment payment = new Payment();

		payment.setPaymentAmount(totalAmount);

		payment.setPaymentMethod(request.getPaymentMethod());

		payment.setPaymentStatus(PaymentStatus.PENDING);

		// 13. Connect payment and order

		payment.setOrder(order);
		order.setPayment(payment);

		// 14. Save complete order

		Orders savedOrder = orderRepository.save(order);

		ResponseStructure<Orders> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.CREATED.value());

		res.setMessage("Order Placed Successfully");

		res.setData(savedOrder);

		return res;
	}

	// =========================================================
	// GET ALL ORDERS
	// =========================================================

	public ResponseStructure<List<Orders>> getAllOrders() {

		List<Orders> orders = orderRepository.findAll();

		if (orders.isEmpty()) {

			throw new NoRecordAvailableException("No Orders Available");
		}

		ResponseStructure<List<Orders>> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.OK.value());

		res.setMessage("Orders Found");

		res.setData(orders);

		return res;
	}

	// =========================================================
	// GET ORDER BY ID
	// =========================================================

	public ResponseStructure<Orders> getOrderById(Integer orderId) {

		Optional<Orders> opt = orderRepository.findById(orderId);

		if (opt.isEmpty()) {

			throw new IdNotFoundException("Order Id " + orderId + " Not Found");
		}

		ResponseStructure<Orders> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.OK.value());

		res.setMessage("Order Found");

		res.setData(opt.get());

		return res;
	}

	// =========================================================
	// GET ALL ORDERS OF CUSTOMER
	// =========================================================

	public ResponseStructure<List<Orders>> getAllOrdersOfCustomer(Integer customerId) {

		Optional<Customer> customerOpt = customerRepository.findById(customerId);

		if (customerOpt.isEmpty()) {

			throw new IdNotFoundException("Customer Id " + customerId + " Not Found");
		}

		List<Orders> orders = orderRepository.findByCustomerId(customerId);

		if (orders.isEmpty()) {

			throw new NoRecordAvailableException("No Orders Found for Customer Id " + customerId);
		}

		ResponseStructure<List<Orders>> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.OK.value());

		res.setMessage("Customer Orders Found");

		res.setData(orders);

		return res;
	}

	// =========================================================
	// UPDATE ORDER STATUS
	// =========================================================

	@Transactional
	public ResponseStructure<Orders> updateOrderStatus(Integer orderId, OrderStatus newStatus) {

		Optional<Orders> opt = orderRepository.findById(orderId);

		if (opt.isEmpty()) {

			throw new IdNotFoundException("Order Id " + orderId + " Not Found");
		}

		Orders order = opt.get();

		OrderStatus currentStatus = order.getStatus();

		// Cancelled order cannot be updated

		if (currentStatus == OrderStatus.CANCELLED) {

			throw new BusinessException("Cancelled order cannot be updated");
		}

		// Delivered order cannot be updated

		if (currentStatus == OrderStatus.DELIVERED) {

			throw new BusinessException("Delivered order cannot be updated");
		}

		// Cancellation must use cancel API

		if (newStatus == OrderStatus.CANCELLED) {

			throw new BusinessException("Use the cancel order API to cancel an order");
		}

		order.setStatus(newStatus);

		Orders updatedOrder = orderRepository.save(order);

		ResponseStructure<Orders> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.OK.value());

		res.setMessage("Order Status Updated Successfully");

		res.setData(updatedOrder);

		return res;
	}

	// =========================================================
	// CANCEL ORDER
	// =========================================================

	@Transactional
	public ResponseStructure<Orders> cancelOrder(Integer orderId) {

		Optional<Orders> opt = orderRepository.findById(orderId);

		if (opt.isEmpty()) {

			throw new IdNotFoundException("Order Id " + orderId + " Not Found");
		}

		Orders order = opt.get();

		OrderStatus currentStatus = order.getStatus();

		// Already cancelled

		if (currentStatus == OrderStatus.CANCELLED) {

			throw new BusinessException("Order is already cancelled");
		}

		// Cannot cancel after preparation

		if (currentStatus == OrderStatus.PREPARING || currentStatus == OrderStatus.READY
				|| currentStatus == OrderStatus.OUT_FOR_DELIVERY || currentStatus == OrderStatus.DELIVERED) {

			throw new BusinessException("Order cannot be cancelled after preparation has started");
		}

		order.setStatus(OrderStatus.CANCELLED);

		Orders cancelledOrder = orderRepository.save(order);

		ResponseStructure<Orders> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.OK.value());

		res.setMessage("Order Cancelled Successfully");

		res.setData(cancelledOrder);

		return res;
	}

	// =========================================================
	// GET ORDERS BY STATUS
	// =========================================================

	public ResponseStructure<List<Orders>> getOrdersByStatus(OrderStatus status) {

		List<Orders> orders = orderRepository.findByStatus(status);

		if (orders.isEmpty()) {

			throw new NoRecordAvailableException("No Orders Found with Status " + status);
		}

		ResponseStructure<List<Orders>> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.OK.value());

		res.setMessage("Orders Found");

		res.setData(orders);

		return res;
	}

	// =========================================================
	// GET ORDERS BY DATE
	// =========================================================

	public ResponseStructure<List<Orders>> getOrdersByDate(LocalDate date) {

		LocalDateTime start = date.atStartOfDay();

		LocalDateTime end = date.plusDays(1).atStartOfDay();

		List<Orders> orders = orderRepository.findByOrderDateTimeBetween(start, end);

		if (orders.isEmpty()) {

			throw new NoRecordAvailableException("No Orders Found on Date " + date);
		}

		ResponseStructure<List<Orders>> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.OK.value());

		res.setMessage("Orders Found");

		res.setData(orders);

		return res;
	}

	// =========================================================
	// GET ALL ORDERS OF RESTAURANT
	// =========================================================

	public ResponseStructure<List<Orders>> getAllOrdersOfRestaurant(Integer restaurantId) {

		Optional<Restaurant> restaurantOpt = restaurantRepository.findById(restaurantId);

		if (restaurantOpt.isEmpty()) {

			throw new IdNotFoundException("Restaurant Id " + restaurantId + " Not Found");
		}

		List<Orders> orders = orderRepository.findByRestaurantId(restaurantId);

		if (orders.isEmpty()) {

			throw new NoRecordAvailableException("No Orders Found for Restaurant Id " + restaurantId);
		}

		ResponseStructure<List<Orders>> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.OK.value());

		res.setMessage("Restaurant Orders Found");

		res.setData(orders);

		return res;
	}
}
