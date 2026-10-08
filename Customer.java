package com.example.foodordering.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Entity
@Table(name = "customers", uniqueConstraints = { @UniqueConstraint(name = "uk_customer_email", columnNames = "email"),
		@UniqueConstraint(name = "uk_customer_contact", columnNames = "contact") })
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotBlank(message = "Name is required")
	private String name;

	@NotBlank(message = "Email is required")
	@Email(message = "Enter a valid email")
	@Column(nullable = false, unique = true)
	private String email;

	@NotBlank(message = "Contact number is required")
	@Pattern(regexp = "^[0-9]{10}$", message = "Contact number must contain exactly 10 digits")
	@Column(nullable = false, unique = true, length = 10)
	private String contact;

	@NotBlank(message = "Address is required")
	private String address;

	@OneToMany(mappedBy = "customer")
	@JsonIgnore
	private List<Orders> orders = new ArrayList<>();

	public Customer() {
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public List<Orders> getOrders() {
		return orders;
	}

	public void setOrders(List<Orders> orders) {
		this.orders = orders;
	}
}