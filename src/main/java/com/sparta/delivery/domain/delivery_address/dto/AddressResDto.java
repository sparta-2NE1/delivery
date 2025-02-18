package com.sparta.delivery.domain.delivery_address.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddressResDto {

    private UUID deliveryAddressId;

    private String deliveryAddress;

    private String deliveryAddressInfo;

    private String detailAddress;
}
