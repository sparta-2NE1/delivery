package com.sparta.delivery.domain.payment.repository;

import com.sparta.delivery.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID>, PaymentRepositoryCustom{

    List<Payment> findByUser_UsernameAndDeletedAtIsNull(String username);

    Optional<Payment> findByPaymentIdAndDeletedAtIsNullAndUser_Username(UUID paymentId, String username);
}
