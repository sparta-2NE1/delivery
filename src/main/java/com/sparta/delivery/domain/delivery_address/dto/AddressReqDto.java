package com.sparta.delivery.domain.delivery_address.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddressReqDto {

    @NotBlank(message = "배송지명은 필수 입력 값 입니다.")
    private String deliveryAddress; //배송지명

    @NotBlank(message = "배송지 주소는 필수 입력 값입 니다.")
    private String deliveryAddressInfo; // 배송지 주소

    private String detailAddress;// 배송지 상세 주소
}
