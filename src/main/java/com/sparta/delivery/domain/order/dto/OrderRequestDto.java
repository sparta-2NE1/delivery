package com.sparta.delivery.domain.order.dto;

import com.sparta.delivery.domain.delivery_address.entity.DeliveryAddress;
import com.sparta.delivery.domain.order.entity.Order;
import com.sparta.delivery.domain.order.enums.OrderStatus;
import com.sparta.delivery.domain.order.enums.OrderType;
import com.sparta.delivery.domain.store.entity.Stores;
import com.sparta.delivery.domain.user.entity.User;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {
    private UUID deliveryAddressId;
    @NotNull(message = "가게Id를 입력해주세요")
    private UUID storeId;
    private List<UUID> productId;
    @NotNull(message = "주문 유형을 입력해주세요")
    private OrderType orderType;
    private String requirements;

    @AssertTrue(message = "배달 주문일 경우 배송 주소 ID가 필요합니다.")
    public boolean isValidDeliveryAddress() {
        if (orderType == OrderType.DELIVERY) {
            return deliveryAddressId != null;
        }
        // 배달이 아닌 경우 deliveryAddressId는 null이어야 함
        return deliveryAddressId == null;
    }

    public Order toOrder(Stores store, DeliveryAddress deliveryAddress, User user) {
        return Order.builder()
                .orderTime(LocalDateTime.now())
                .orderType(orderType)
                //주문 생성 시 기본 상태는 결제 대기
                .orderStatus(OrderStatus.PAYMENT_WAIT)
                .requirements(requirements)
                .stores(store)
                .deliveryAddress(deliveryAddress)
                .user(user).build();
    }
}
