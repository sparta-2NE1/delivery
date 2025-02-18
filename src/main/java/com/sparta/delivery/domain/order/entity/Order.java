package com.sparta.delivery.domain.order.entity;

import com.sparta.delivery.domain.common.Timestamped;
import com.sparta.delivery.domain.delivery_address.entity.DeliveryAddress;
import com.sparta.delivery.domain.order.dto.OrderResponseDto;
import com.sparta.delivery.domain.order.enums.OrderStatus;
import com.sparta.delivery.domain.order.enums.OrderType;
import com.sparta.delivery.domain.payment.entity.Payment;
import com.sparta.delivery.domain.store.entity.Stores;
import com.sparta.delivery.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "p_order")
public class Order extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID orderId;

    @Column(nullable = false)
    private LocalDateTime orderTime;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private OrderType orderType;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private OrderStatus orderStatus;

    @Column
    private String requirements;

    @ManyToOne
    @JoinColumn(name = "storeId", nullable = false)
    private Stores stores;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "deliveryAddressId")
    private DeliveryAddress deliveryAddress;

    public OrderResponseDto toResponseDto() {
        return new OrderResponseDto(
                this.orderId,
                this.orderTime,
                this.orderType,
                this.orderStatus,
                this.requirements,
                this.stores,
                this.user
        );
    }

}
