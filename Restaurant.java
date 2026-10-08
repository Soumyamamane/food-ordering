package com.example.foodordering.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })

@Entity
@Table(name = "restaurants")
public class Restaurant {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotBlank(message = "Restaurant name is required")
	@Column(nullable = false)
	private String name;

	@NotBlank(message = "Location is required")
	@Column(nullable = false)
	private String location;

	@DecimalMin(value = "0.0", message = "Rating cannot be negative")
	@DecimalMax(value = "5.0", message = "Rating cannot be greater than 5")
	private Double rating;

	@OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<MenuItem> menuItems = new ArrayList<>();

	@OneToMany(mappedBy = "restaurant")
	@JsonIgnore
	private List<Orders> orders = new ArrayList<>();

	public Restaurant() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	public List<MenuItem> getMenuItems() {
		return menuItems;
	}

	public void setMenuItems(List<MenuItem> menuItems) {
		this.menuItems = menuItems;
	}

	public List<Orders> getOrders() {
		return orders;
	}

	public void setOrders(List<Orders> orders) {
		this.orders = orders;
	}
}