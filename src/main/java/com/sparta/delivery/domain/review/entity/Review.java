package com.sparta.delivery.domain.review.entity;


import com.sparta.delivery.domain.common.Timestamped;
import com.sparta.delivery.domain.order.entity.Order;
import com.sparta.delivery.domain.review.dto.ReviewResponseDto;
import com.sparta.delivery.domain.store.entity.Stores;
import com.sparta.delivery.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "p_review")
public class Review extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID reviewId;

    @Column(nullable = false)
    private String comment;

    @Column(nullable = false)
    private int star;

    @OneToOne
    @JoinColumn(name = "orderId", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "storeId", nullable = false)
    private Stores stores;

    public ReviewResponseDto toResponseDto() {
        return new ReviewResponseDto(
                this.reviewId,
                this.comment,
                this.star,
                this.order.getOrderId(),
                this.user.getUserId(),
                this.stores.getStoreId()
        );
    }
}
