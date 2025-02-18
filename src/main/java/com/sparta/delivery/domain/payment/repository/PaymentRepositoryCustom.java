package com.sparta.delivery.domain.payment.repository;
import com.sparta.delivery.domain.payment.dto.SearchDto;
import com.sparta.delivery.domain.payment.entity.Payment;
import java.util.List;

public interface PaymentRepositoryCustom {
    List<Payment> searchPayments(SearchDto searchDto, String username);
}
