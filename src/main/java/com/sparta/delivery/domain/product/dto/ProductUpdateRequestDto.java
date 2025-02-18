package com.sparta.delivery.domain.product.dto;

import lombok.Getter;

@Getter
public class ProductUpdateRequestDto {

    private String name;

    private String description;

    private int price;

    private int quantity;

    private boolean hidden;
}
