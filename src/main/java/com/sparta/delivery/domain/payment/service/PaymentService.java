package com.sparta.delivery.domain.payment.service;

import com.sparta.delivery.domain.card.entity.Card;
import com.sparta.delivery.domain.card.repository.CardRepository;
import com.sparta.delivery.domain.order.entity.Order;
import com.sparta.delivery.domain.order.enums.OrderStatus;
import com.sparta.delivery.domain.order.repository.OrderRepository;
import com.sparta.delivery.domain.payment.dto.PaymentDto;
import com.sparta.delivery.domain.payment.dto.RegisterPaymentDto;
import com.sparta.delivery.domain.payment.dto.SearchDto;
import com.sparta.delivery.domain.payment.entity.Payment;
import com.sparta.delivery.domain.payment.repository.PaymentRepository;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public String isRegisterPayment(RegisterPaymentDto registerPaymentDto, String username) {
        // 결제 내역만 관리하므로 직접 결제는 필요 없지만, 데이터 저장을 위해 생성
        Card card = cardRepository.findByCardIdAndDeletedAtIsNull(registerPaymentDto.getCardId())
                .orElseThrow(() -> new NullPointerException("카드가 존재하지 않습니다"));
        // TODO : Order Deleted 검증 필요
        Order order = orderRepository.findById(registerPaymentDto.getOrderId())
                .orElseThrow(() -> new NullPointerException("주문이 존재하지 않습니다"));
        User user = userRepository.findByUsername(username).orElseThrow(() -> new NullPointerException("유저가 존재하지 않습니다"));
        order.setOrderStatus(OrderStatus.PAYMENT_COMPLETE);
        try {
            paymentRepository.save(Payment.builder()
                    .user(user)
                    .card(card)
                    .order(order)
                    .amount(registerPaymentDto.getAmount())
                    .build());
        } catch (Exception e) {
            return "결제 실패 " + e.getMessage();
        }
        return "결제 성공";
    }

    public PaymentDto getPayment(UUID paymentId) {
        Payment payment = paymentRepository.findByPaymentIdAndDeletedAtIsNull(paymentId).orElseThrow(()
                -> new NullPointerException("결제 내역이 존재하지 않습니다."));
        // TODO : Order Deleted 검증 필요
        Order order = orderRepository.findById(payment.getOrder().getOrderId()).orElseThrow(()
                -> new NullPointerException("주문이 존재하지 않습니다."));

        PaymentDto paymentDto = PaymentDto.builder()
                .paymentId(paymentId)
                .amount(payment.getAmount())
                .orderId(order.getOrderId())
                .orderTime(order.getOrderTime())
                .orderType(order.getOrderType())
                .orderStatus(order.getOrderStatus())
                .requirements(order.getRequirements())
                .build();
        return paymentDto;
    }

    public List<PaymentDto> getPayments(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new NullPointerException("유저 정보가 없습니다.")
        );
        List<PaymentDto> paymentDtos = new ArrayList<>();

        // TODO User에 List<Payment> 생기면 작업
        //  List<Payment> payments = user.getPayments();

        // 만들지 않은 경우
        List<Payment> payments = paymentRepository.findByUser_UsernameAndDeletedAtIsNull(username);
        for (Payment payment : payments) {
            paymentDtos.add(getPayment(payment.getPaymentId()));
        }

        return paymentDtos;
    }

    // TODO : QueryDSL을 이용해 구현 예정
    public List<SearchDto> searchPayments(SearchDto searchDto) {
        return null;
    }

    @Transactional
    public String deletePayment(UUID paymentId, String username) {
        Payment payment = paymentRepository.findByPaymentIdAndDeletedAtIsNull(paymentId).orElseThrow(() ->
                new NullPointerException("결제 정보가 존재하지 않습니다."));
        if (payment.getDeletedAt() != null) {
            return "결제 정보가 존재하지 않습니다.";
        }

        try {
            payment.setDeletedAt(LocalDateTime.now());
            payment.setDeletedBy(username);
            paymentRepository.save(payment);
        } catch (Exception e) {
            return "결제 내역 삭제 실패 " + e.getMessage();
        }
        return "결제 내역 삭제 성공";
    }
}
