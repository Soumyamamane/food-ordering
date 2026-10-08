package com.example.foodordering.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.foodordering.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    Optional<Customer> findByContact(String contact);

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByNameIgnoreCase(String name);
}