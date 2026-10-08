package com.example.foodordering.service;

import com.example.foodordering.dto.ResponseStructure;
import com.example.foodordering.entity.OrderStatus;
import com.example.foodordering.entity.Orders;
import com.example.foodordering.entity.Payment;
import com.example.foodordering.entity.PaymentMethod;
import com.example.foodordering.entity.PaymentStatus;
import com.example.foodordering.exception.BusinessException;
import com.example.foodordering.exception.IdNotFoundException;
import com.example.foodordering.exception.NoRecordAvailableException;
import com.example.foodordering.repository.OrderRepository;
import com.example.foodordering.repository.PaymentRepository;

import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

	private final PaymentRepository paymentRepository;
	private final OrderRepository orderRepository;

	public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository) {

		this.paymentRepository = paymentRepository;
		this.orderRepository = orderRepository;
	}

// =========================================================
// GET PAYMENT BY ID
// =========================================================

	public ResponseStructure<Payment> getPaymentById(Integer paymentId) {

		Optional<Payment> opt = paymentRepository.findById(paymentId);

		if (opt.isEmpty()) {
			throw new IdNotFoundException("Payment with ID " + paymentId + " not found");
		}

		ResponseStructure<Payment> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.OK.value());
		res.setMessage("Payment Found");
		res.setData(opt.get());

		return res;
	}

// =========================================================
// GET PAYMENT BY ORDER
// =========================================================

	public ResponseStructure<Payment> getPaymentByOrder(Integer orderId) {

		// Verify order exists

		Optional<Orders> orderOpt = orderRepository.findById(orderId);

		if (orderOpt.isEmpty()) {
			throw new IdNotFoundException("Order with ID " + orderId + " not found");
		}

		// Find payment for order

		Optional<Payment> paymentOpt = paymentRepository.findByOrderOrderId(orderId);

		if (paymentOpt.isEmpty()) {
			throw new NoRecordAvailableException("Payment not found for order ID " + orderId);
		}

		ResponseStructure<Payment> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.OK.value());
		res.setMessage("Payment Found");
		res.setData(paymentOpt.get());

		return res;
	}

// =========================================================
// GET PAYMENT BY STATUS
// =========================================================

	public ResponseStructure<List<Payment>> getPaymentByStatus(PaymentStatus status) {

		List<Payment> payments = paymentRepository.findByPaymentStatus(status);

		if (payments.isEmpty()) {
			throw new NoRecordAvailableException("No payments found with status " + status);
		}

		ResponseStructure<List<Payment>> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.OK.value());
		res.setMessage("Payments Found");
		res.setData(payments);

		return res;
	}

// =========================================================
// GET PAYMENT BY METHOD
// =========================================================

	public ResponseStructure<List<Payment>> getPaymentByMethod(PaymentMethod method) {

		List<Payment> payments = paymentRepository.findByPaymentMethod(method);

		if (payments.isEmpty()) {
			throw new NoRecordAvailableException("No payments found with method " + method);
		}

		ResponseStructure<List<Payment>> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.OK.value());
		res.setMessage("Payments Found");
		res.setData(payments);

		return res;
	}

// =========================================================
// UPDATE PAYMENT STATUS
// =========================================================

	@Transactional
	public ResponseStructure<Payment> updatePaymentStatus(Integer paymentId, PaymentStatus newStatus) {

		Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);

		if (paymentOpt.isEmpty()) {
			throw new IdNotFoundException("Payment with ID " + paymentId + " not found");
		}

		Payment payment = paymentOpt.get();

		Orders order = payment.getOrder();

		// -----------------------------------------------------
		// Check payment amount and order total
		// -----------------------------------------------------

		BigDecimal paymentAmount = payment.getPaymentAmount();

		BigDecimal orderTotal = order.getTotalAmount();

		if (paymentAmount == null || orderTotal == null || paymentAmount.compareTo(orderTotal) != 0) {

			throw new BusinessException("Payment amount must be equal to order total amount");
		}

		// -----------------------------------------------------
		// Prevent payment changes for cancelled order
		// -----------------------------------------------------

		if (order.getStatus() == OrderStatus.CANCELLED) {

			throw new BusinessException("Payment status cannot be updated for a cancelled order");
		}

		// -----------------------------------------------------
		// Prevent changes after delivery
		// -----------------------------------------------------

		if (order.getStatus() == OrderStatus.DELIVERED && newStatus != PaymentStatus.REFUNDED) {

			throw new BusinessException("Payment status cannot be changed after order delivery");
		}

		// -----------------------------------------------------
		// Update payment status
		// -----------------------------------------------------

		payment.setPaymentStatus(newStatus);

		Payment savedPayment = paymentRepository.save(payment);

		ResponseStructure<Payment> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.OK.value());
		res.setMessage("Payment Status Updated Successfully");
		res.setData(savedPayment);

		return res;
	}

}
