package com.example.foodordering.controller;

import com.example.foodordering.dto.ResponseStructure;
import com.example.foodordering.entity.Payment;
import com.example.foodordering.entity.PaymentMethod;
import com.example.foodordering.entity.PaymentStatus;
import com.example.foodordering.service.PaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

	@Autowired
	private PaymentService paymentService;

// =========================================================
// GET PAYMENT BY ID
// =========================================================

	@GetMapping("/{paymentId}")
	public ResponseEntity<ResponseStructure<Payment>> getPaymentById(@PathVariable Integer paymentId) {

		return new ResponseEntity<>(paymentService.getPaymentById(paymentId), HttpStatus.OK);
	}

// =========================================================
// GET PAYMENT BY ORDER
// =========================================================

	@GetMapping("/order/{orderId}")
	public ResponseEntity<ResponseStructure<Payment>> getPaymentByOrder(@PathVariable Integer orderId) {

		return new ResponseEntity<>(paymentService.getPaymentByOrder(orderId), HttpStatus.OK);
	}

// =========================================================
// GET PAYMENT BY STATUS
// =========================================================

	@GetMapping("/status/{status}")
	public ResponseEntity<ResponseStructure<List<Payment>>> getPaymentByStatus(@PathVariable PaymentStatus status) {

		return new ResponseEntity<>(paymentService.getPaymentByStatus(status), HttpStatus.OK);
	}

// =========================================================
// GET PAYMENT BY METHOD
// =========================================================

	@GetMapping("/method/{method}")
	public ResponseEntity<ResponseStructure<List<Payment>>> getPaymentByMethod(@PathVariable PaymentMethod method) {

		return new ResponseEntity<>(paymentService.getPaymentByMethod(method), HttpStatus.OK);
	}

// =========================================================
// UPDATE PAYMENT STATUS
// =========================================================

	@PutMapping("/{paymentId}/status")
	public ResponseEntity<ResponseStructure<Payment>> updatePaymentStatus(@PathVariable Integer paymentId,
			@RequestParam PaymentStatus status) {

		return new ResponseEntity<>(paymentService.updatePaymentStatus(paymentId, status), HttpStatus.OK);
	}

}
