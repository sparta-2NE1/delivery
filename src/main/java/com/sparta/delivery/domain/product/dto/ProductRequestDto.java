package com.sparta.delivery.domain.product.dto;

import com.sparta.delivery.domain.product.entity.Product;
import com.sparta.delivery.domain.store.entity.Stores;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class ProductRequestDto {

    @NotBlank(message = "상품명은 필수 입력 값입니다.")
    private String name;

    private String description;

    @Min(value = 0, message = "상품 가격은 0 원 이상이어야 합니다.")
    private int price;

    private int quantity;

    @NotNull(message = "상품 숨김 여부는 필수 입력 값입니다.")
    private Boolean hidden;

    public Product toEntity(UUID storeId) {
        return Product.builder()
                .name(this.name)
                .description(this.description)
                .price(this.price)
                .quantity(this.quantity)
                .hidden(this.hidden)
                .store(Stores.builder().storeId(storeId).build())
                .build();
    }
}
