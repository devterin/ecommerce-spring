package com.devterin.repository;

import com.devterin.entity.Payment;
import com.devterin.enums.PaymentMethod;
import com.devterin.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Payment p WHERE p.vnpayTxnRef = :txnRef")
    Optional<Payment> findByVnpayTxnRefForUpdate(@Param("txnRef") String txnRef);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Payment p WHERE p.id = :paymentId")
    Optional<Payment> findByIdForUpdate(@Param("paymentId") Long paymentId);

    List<Payment> findAllByPaymentMethodAndPaymentStatusAndVnpayExpireAtBefore(
            PaymentMethod paymentMethod,
            PaymentStatus paymentStatus,
            LocalDateTime expireAt);
}
