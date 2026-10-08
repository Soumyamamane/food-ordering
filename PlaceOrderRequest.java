package com.example.foodordering.dto;

import com.example.foodordering.entity.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class PlaceOrderRequest {

	@NotNull(message = "Customer ID is required")
	private Integer customerId;

	@NotNull(message = "Restaurant ID is required")
	private Integer restaurantId;

	@NotEmpty(message = "Order must contain at least one order item")
	@Valid
	private List<OrderItemRequest> orderItems;

	@NotNull(message = "Payment method is required")
	private PaymentMethod paymentMethod;

	public PlaceOrderRequest() {
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public Integer getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(Integer restaurantId) {
		this.restaurantId = restaurantId;
	}

	public List<OrderItemRequest> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<OrderItemRequest> orderItems) {
		this.orderItems = orderItems;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
}