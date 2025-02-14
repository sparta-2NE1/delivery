package com.sparta.delivery.domain.order.entity;

import com.sparta.delivery.domain.common.Timestamped;
import com.sparta.delivery.domain.order.enums.OrderStatus;
import com.sparta.delivery.domain.order.enums.OrderType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "p_order")
public class Order extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long order_id;

    @Column(nullable = false)
    private LocalDateTime order_time;

    @Column(nullable = false)
    private OrderType order_type;

    @Column(nullable = false)
    private OrderStatus order_status;

    @Column(nullable = false)
    private boolean delivery_type;

    @Column
    @Enumerated(EnumType.STRING)
    private String requirements;

    //Entity 모두 완료 후 연관관계 Column 추가 예정



}
