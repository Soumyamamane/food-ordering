package com.example.foodordering.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.foodordering.entity.OrderStatus;
import com.example.foodordering.entity.Orders;

public interface OrderRepository extends JpaRepository<Orders, Integer> {

	List<Orders> findByCustomerId(Integer customerId);

	List<Orders> findByRestaurantId(Integer restaurantId);

	List<Orders> findByStatus(OrderStatus status);

	List<Orders> findByOrderDateTimeBetween(LocalDateTime start, LocalDateTime end);

	long countByCustomerIdAndStatus(Integer customerId, OrderStatus status);

	boolean existsByCustomerIdAndStatusIn(Integer customerId, List<OrderStatus> statuses);
}