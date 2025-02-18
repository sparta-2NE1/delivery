package com.sparta.delivery.domain.delivery_address.entity;

import com.sparta.delivery.domain.common.Timestamped;
import com.sparta.delivery.domain.delivery_address.dto.AddressResDto;
import com.sparta.delivery.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "p_delivery_address")
public class DeliveryAddress extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "delivery_address_id")
    private UUID deliveryAddressId;

    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    @Column(name = "delivery_address_info", nullable = false)
    private String deliveryAddressInfo;

    @Column(name = "detail_address")
    private String detailAddress;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public AddressResDto toResponse() {

        return new AddressResDto(
                this.deliveryAddress,
                this.deliveryAddressInfo,
                this.detailAddress
        );
    }
}
