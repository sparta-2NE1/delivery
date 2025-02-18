package com.sparta.delivery.domain.payment.dto;

import com.sparta.delivery.domain.order.enums.OrderStatus;
import com.sparta.delivery.domain.order.enums.OrderType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Data
@Builder
public class SearchDto {
    private Integer minAmount;
    private Integer maxAmount;
    private OrderStatus orderStatus;
    private OrderType orderType;
    private LocalDateTime paymentTime;
    private String cardCompany;

    public SearchDto(Integer minAmount, Integer maxAmount, OrderStatus orderStatus, OrderType orderType, LocalDateTime paymentTime, String cardCompany) {
        this.minAmount = minAmount == null ? 0 : minAmount;
        this.maxAmount = maxAmount == null ? Integer.MAX_VALUE : maxAmount;
        this.orderStatus = orderStatus;
        this.orderType = orderType;
        this.paymentTime = paymentTime;
        this.cardCompany = cardCompany;
    }
}
