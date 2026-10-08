package com.example.foodordering.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.foodordering.entity.Payment;
import com.example.foodordering.entity.PaymentMethod;
import com.example.foodordering.entity.PaymentStatus;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    Optional<Payment> findByOrderOrderId(Integer orderId);

    List<Payment> findByPaymentStatus(PaymentStatus paymentStatus);

    List<Payment> findByPaymentMethod(PaymentMethod paymentMethod);
}