package com.sparta.delivery.domain.order.entity;

import com.sparta.delivery.domain.common.Timestamped;
import com.sparta.delivery.domain.delivery_address.entity.DeliveryAddress;
import com.sparta.delivery.domain.order.dto.OrderResponseDto;
import com.sparta.delivery.domain.order.enums.OrderStatus;
import com.sparta.delivery.domain.order.enums.OrderType;
import com.sparta.delivery.domain.orderProduct.entity.OrderProduct;
import com.sparta.delivery.domain.payment.entity.Payment;
import com.sparta.delivery.domain.product.entity.Product;
import com.sparta.delivery.domain.store.entity.Stores;
import com.sparta.delivery.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "p_order")
public class Order extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID orderId;

    @Column(nullable = false)
    private LocalDateTime orderTime;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private OrderType orderType;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private OrderStatus orderStatus;

    @Column
    private String requirements;

    @ManyToOne
    @JoinColumn(name = "storeId", nullable = false)
    private Stores stores;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "deliveryAddressId", nullable = false)
    private DeliveryAddress deliveryAddress;

    //주문상품 테이블과의 연관관계 매핑. 필요 시 사용
    @OneToMany(mappedBy = "order", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<OrderProduct> orderProductList;

    public void updateOrderProductList(List<OrderProduct> newList) {
        orderProductList.clear();
        for(OrderProduct orderProduct : newList)
            orderProductList.add(orderProduct);
    }

    public OrderResponseDto toResponseDto() {
        return new OrderResponseDto(
                this.orderId,
                this.orderTime,
                this.orderType,
                this.orderStatus,
                this.requirements,
                this.stores.getStoreId(),
                this.user.getUserId(),
                this.deliveryAddress.getDeliveryAddressId()
        );
    }

}
