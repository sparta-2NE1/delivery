package com.sparta.delivery.domain.product.dto;

import com.sparta.delivery.domain.product.entity.Product;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class ProductResponseDto {

    private UUID productId;

    private String name;

    private String description;

    private int price;

    private int quantity;

    private boolean hidden;

    private UUID storeId;

    public static ProductResponseDto from(Product product) {
        return ProductResponseDto.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .hidden(product.isHidden())
                .storeId(product.getStore().getStoreId())
                .build();
    }
}
