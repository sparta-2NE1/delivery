package com.sparta.delivery.domain.order.entity;

import com.sparta.delivery.domain.common.Timestamped;
import com.sparta.delivery.domain.order.enums.OrderStatus;
import com.sparta.delivery.domain.order.enums.OrderType;
import com.sparta.delivery.domain.payment.entity.Payment;
import com.sparta.delivery.domain.store.entity.Stores;
import com.sparta.delivery.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
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

    @OneToOne
    @JoinColumn(name = "paymentId")
    private Payment payment;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    //배송지정보 연관관계 추가 필요

}
