package com.example.foodordering.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.foodordering.dto.CustomerRequest;
import com.example.foodordering.dto.ResponseStructure;
import com.example.foodordering.entity.Customer;
import com.example.foodordering.entity.OrderStatus;
import com.example.foodordering.exception.BusinessException;
import com.example.foodordering.exception.IdNotFoundException;
import com.example.foodordering.exception.NoRecordAvailableException;
import com.example.foodordering.repository.CustomerRepository;
import com.example.foodordering.repository.OrderRepository;

@Service
public class CustomerService {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private OrderRepository orderRepository;

	// CREATE CUSTOMER
	public ResponseStructure<Customer> createCustomer(CustomerRequest request) {

		if (customerRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new BusinessException("Customer with email " + request.getEmail() + " already exists");
		}

		if (customerRepository.findByContact(request.getContact()).isPresent()) {
			throw new BusinessException("Customer with contact " + request.getContact() + " already exists");
		}

		Customer customer = new Customer();

		customer.setName(request.getName());
		customer.setEmail(request.getEmail());
		customer.setContact(request.getContact());
		customer.setAddress(request.getAddress());

		Customer savedCustomer = customerRepository.save(customer);

		ResponseStructure<Customer> res = new ResponseStructure<>();
		res.setStatusCode(HttpStatus.CREATED.value());
		res.setMessage("Customer Saved Successfully");
		res.setData(savedCustomer);

		return res;
	}

	// GET ALL CUSTOMERS
	public ResponseStructure<List<Customer>> getAllCustomers() {

		List<Customer> customers = customerRepository.findAll();

		if (customers.isEmpty()) {
			throw new NoRecordAvailableException("No Customers Available");
		}

		ResponseStructure<List<Customer>> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.OK.value());
		res.setMessage("All Customers Fetched Successfully");
		res.setData(customers);

		return res;
	}

	// GET CUSTOMER BY ID
	public ResponseStructure<Customer> getCustomerById(Integer id) {

		Optional<Customer> opt = customerRepository.findById(id);

		if (opt.isEmpty()) {
			throw new IdNotFoundException("Customer Id " + id + " Not Found");
		}

		ResponseStructure<Customer> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.OK.value());
		res.setMessage("Customer Found Successfully");
		res.setData(opt.get());

		return res;
	}

	// UPDATE CUSTOMER
	public ResponseStructure<Customer> updateCustomer(Integer id, CustomerRequest request) {

		Optional<Customer> opt = customerRepository.findById(id);

		if (opt.isEmpty()) {
			throw new IdNotFoundException("Customer Id " + id + " Not Found");
		}

		Customer customer = opt.get();

		// Check duplicate email
		Optional<Customer> existingEmail = customerRepository.findByEmail(request.getEmail());

		if (existingEmail.isPresent() && !existingEmail.get().getId().equals(id)) {

			throw new BusinessException("Customer with email " + request.getEmail() + " already exists");
		}

		// Check duplicate contact
		Optional<Customer> existingContact = customerRepository.findByContact(request.getContact());

		if (existingContact.isPresent() && !existingContact.get().getId().equals(id)) {

			throw new BusinessException("Customer with contact " + request.getContact() + " already exists");
		}

		customer.setName(request.getName());
		customer.setEmail(request.getEmail());
		customer.setContact(request.getContact());
		customer.setAddress(request.getAddress());

		Customer updatedCustomer = customerRepository.save(customer);

		ResponseStructure<Customer> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.OK.value());
		res.setMessage("Customer Updated Successfully");
		res.setData(updatedCustomer);

		return res;
	}

	// DELETE CUSTOMER
	@Transactional
	public ResponseStructure<String> deleteCustomer(Integer id) {

		Optional<Customer> opt = customerRepository.findById(id);

		if (opt.isEmpty()) {
			throw new IdNotFoundException("Customer Id " + id + " Not Found");
		}

		boolean hasCompletedOrder = orderRepository.existsByCustomerIdAndStatusIn(id, List.of(OrderStatus.DELIVERED));

		if (!hasCompletedOrder) {

			throw new BusinessException("Customer cannot be deleted because " + "the customer has no completed order");
		}

		customerRepository.delete(opt.get());

		ResponseStructure<String> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.OK.value());
		res.setMessage("Customer Deleted Successfully");
		res.setData("Deleted");

		return res;
	}

	// GET BY CONTACT
	public ResponseStructure<Customer> getCustomerByContact(String contact) {

		Optional<Customer> opt = customerRepository.findByContact(contact);

		if (opt.isEmpty()) {
			throw new IdNotFoundException("Customer with contact " + contact + " Not Found");
		}

		ResponseStructure<Customer> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.OK.value());
		res.setMessage("Customer Found Successfully");
		res.setData(opt.get());

		return res;
	}

	// GET BY EMAIL
	public ResponseStructure<Customer> getCustomerByEmail(String email) {

		Optional<Customer> opt = customerRepository.findByEmail(email);

		if (opt.isEmpty()) {
			throw new IdNotFoundException("Customer with email " + email + " Not Found");
		}

		ResponseStructure<Customer> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.OK.value());
		res.setMessage("Customer Found Successfully");
		res.setData(opt.get());

		return res;
	}

	// GET BY NAME
	public ResponseStructure<Customer> getCustomerByName(String name) {

		Optional<Customer> opt = customerRepository.findByNameIgnoreCase(name);

		if (opt.isEmpty()) {
			throw new IdNotFoundException("Customer with name " + name + " Not Found");
		}

		ResponseStructure<Customer> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.OK.value());
		res.setMessage("Customer Found Successfully");
		res.setData(opt.get());

		return res;
	}
}
