package com.sparta.delivery.domain.delivery_address.entity;

import com.sparta.delivery.domain.common.Timestamped;
import com.sparta.delivery.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "p_delivery_address")
public class DeliveryAddress extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "delivery_address_id")
    private UUID deliveryAddressId;

    @Column(name = "delivery_address", nullable = false) // ✅ 컬럼명을 소문자로 고정
    private String deliveryAddress;

    @Column(name = "delivery_address_info", nullable = false) // ✅ Snake_case로 변경
    private String deliveryAddressInfo;

    @Column(name = "detail_address")
    private String detailAddress;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
