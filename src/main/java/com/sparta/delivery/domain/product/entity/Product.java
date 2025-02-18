package com.sparta.delivery.domain.product.entity;

import com.sparta.delivery.domain.common.Timestamped;
import com.sparta.delivery.domain.product.dto.ProductUpdateRequestDto;
import com.sparta.delivery.domain.store.entity.Stores;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "p_product")
public class Product extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Stores store;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false)
    private int price;

    @Column(nullable = true)
    private int quantity;

    @Column(nullable = false)
    private boolean hidden;

    public void update(ProductUpdateRequestDto productUpdateRequestDto) {
        this.name = productUpdateRequestDto.getName() == null ? this.name : productUpdateRequestDto.getName();
        this.description = productUpdateRequestDto.getDescription() == null ? this.description : productUpdateRequestDto.getDescription();
        this.price = (productUpdateRequestDto.getPrice() == 0 || productUpdateRequestDto.getPrice() < 0) ? this.price : productUpdateRequestDto.getPrice();
        this.quantity = productUpdateRequestDto.getQuantity() == 0 ? this.quantity : productUpdateRequestDto.getQuantity();
        this.hidden = productUpdateRequestDto.isHidden();
    }
}
