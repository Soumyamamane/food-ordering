package com.example.foodordering.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.foodordering.dto.CustomerRequest;
import com.example.foodordering.dto.ResponseStructure;
import com.example.foodordering.entity.Customer;
import com.example.foodordering.service.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	// CREATE CUSTOMER
	@PostMapping
	public ResponseEntity<ResponseStructure<Customer>> createCustomer(@Valid @RequestBody CustomerRequest request) {

		return new ResponseEntity<>(customerService.createCustomer(request), HttpStatus.CREATED);
	}

	// GET ALL CUSTOMERS
	@GetMapping
	public ResponseEntity<ResponseStructure<List<Customer>>> getAllCustomers() {

		return new ResponseEntity<>(customerService.getAllCustomers(), HttpStatus.OK);
	}

	// GET CUSTOMER BY ID
	@GetMapping("/{id}")
	public ResponseEntity<ResponseStructure<Customer>> getCustomerById(@PathVariable Integer id) {

		return new ResponseEntity<>(customerService.getCustomerById(id), HttpStatus.OK);
	}

	// UPDATE CUSTOMER
	@PutMapping("/{id}")
	public ResponseEntity<ResponseStructure<Customer>> updateCustomer(@PathVariable Integer id,
			@Valid @RequestBody CustomerRequest request) {

		return new ResponseEntity<>(customerService.updateCustomer(id, request), HttpStatus.OK);
	}

	// DELETE CUSTOMER
	@DeleteMapping("/{id}")
	public ResponseEntity<ResponseStructure<String>> deleteCustomer(@PathVariable Integer id) {

		return new ResponseEntity<>(customerService.deleteCustomer(id), HttpStatus.OK);
	}

	// GET BY CONTACT
	@GetMapping("/contact/{contact}")
	public ResponseEntity<ResponseStructure<Customer>> getCustomerByContact(@PathVariable String contact) {

		return new ResponseEntity<>(customerService.getCustomerByContact(contact), HttpStatus.OK);
	}

	// GET BY EMAIL
	@GetMapping("/email/{email}")
	public ResponseEntity<ResponseStructure<Customer>> getCustomerByEmail(@PathVariable String email) {

		return new ResponseEntity<>(customerService.getCustomerByEmail(email), HttpStatus.OK);
	}

	// GET BY NAME
	@GetMapping("/name/{name}")
	public ResponseEntity<ResponseStructure<Customer>> getCustomerByName(@PathVariable String name) {

		return new ResponseEntity<>(customerService.getCustomerByName(name), HttpStatus.OK);
	}
}
