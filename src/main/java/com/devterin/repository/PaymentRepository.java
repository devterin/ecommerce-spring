package com.devterin.repository;

import com.devterin.entity.Payment;
import com.devterin.utils.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
