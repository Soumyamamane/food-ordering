package com.example.foodordering.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MenuItemRequest {

	@NotBlank(message = "Item name is required")
	private String itemName;

	@NotNull(message = "Price is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "Price cannot be negative")
	private BigDecimal price;

	@NotNull(message = "Availability is required")
	private Boolean availability;

	/*
	 * Do NOT put @NotNull here.
	 *
	 * restaurantId comes from the URL:
	 *
	 * /api/menu-items/restaurant/{restaurantId}
	 *
	 * and is set by the controller after validation.
	 */
	private Integer restaurantId;

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Boolean getAvailability() {
		return availability;
	}

	public void setAvailability(Boolean availability) {
		this.availability = availability;
	}

	public Integer getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(Integer restaurantId) {
		this.restaurantId = restaurantId;
	}
}