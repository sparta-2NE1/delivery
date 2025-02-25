package com.sparta.delivery.domain.orderProduct.entity;

import com.sparta.delivery.domain.order.entity.Order;
import com.sparta.delivery.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_orderProduct")
public class OrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID orderProductId;

    @ManyToOne
    @JoinColumn(name = "orderId", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    public OrderProduct(Order order, Product product) {
        this.order = order;
        this.product = product;
    }
}
