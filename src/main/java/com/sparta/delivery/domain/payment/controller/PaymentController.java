package com.sparta.delivery.domain.payment.controller;

import com.sparta.delivery.config.auth.PrincipalDetails;
import com.sparta.delivery.domain.order.enums.OrderStatus;
import com.sparta.delivery.domain.order.enums.OrderType;
import com.sparta.delivery.domain.payment.dto.RegisterPaymentDto;
import com.sparta.delivery.domain.payment.dto.SearchDto;
import com.sparta.delivery.domain.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
@Tag(name = "Payment API")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "결제")
    @PostMapping
    public ResponseEntity<?> requestPayment(@RequestBody RegisterPaymentDto registerPaymentDto, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        paymentService.isRegisterPayment(registerPaymentDto, principalDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "결제 내역 조회")
    @GetMapping("/{payment_id}")
    public ResponseEntity<?> getPayment(@PathVariable UUID payment_id, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok().body(paymentService.getPayment(payment_id, principalDetails.getUsername()));
    }

    @Operation(summary = "결제 내역 리스트 조회")
    @GetMapping("/payments")
    public ResponseEntity<?> getPayments(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok().body(paymentService.getPayments(principalDetails.getUsername()));
    }

    @Operation(summary = "결제 내역 검색")
    @GetMapping("/search")
    public ResponseEntity<?> searchPayments(
            @RequestParam(required = false) Integer minAmount,
            @RequestParam(required = false) Integer maxAmount,
            @RequestParam(required = false) OrderStatus orderStatus,
            @RequestParam(required = false) OrderType orderType,
            @RequestParam(required = false) LocalDateTime paymentTime,
            @RequestParam(required = false) String cardCompany,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        SearchDto searchDto = new SearchDto(minAmount, maxAmount, orderStatus, orderType, paymentTime, cardCompany);
        return ResponseEntity.ok().body(paymentService.searchPayments(searchDto, principalDetails.getUsername()));
    }

    @Operation(summary = "결제 내역 삭제")
    @PatchMapping("/{payment_id}")
    public ResponseEntity<?> deletePayment(@PathVariable UUID payment_id, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        paymentService.deletePayment(payment_id, principalDetails.getUsername());
        return ResponseEntity.ok().build();
    }

}