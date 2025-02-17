package com.sparta.delivery.domain.delivery_address;

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
    private UUID delivery_address_id;

    @Column(nullable = false)
    private String delivery_address;

    @Column(nullable = false)
    private String delivery_address_info;

    private String detail_address; // 상세 주소

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
