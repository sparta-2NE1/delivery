package com.sparta.delivery.domain.delivery_address.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressSearchDto {
    private int size = 10;         // 페이지 크기
    private int page = 0;          // 페이지 번호
    private String sortBy = "createdAt"; // 정렬 기준
    private String order = "desc"; // 정렬 방향 (asc 또는 desc)
    private String deliveryAddress;
    private String deliveryAddressInfo;
}
