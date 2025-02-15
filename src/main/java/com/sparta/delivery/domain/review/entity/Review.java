package com.sparta.delivery.domain.review.entity;


import com.sparta.delivery.domain.common.Timestamped;
import com.sparta.delivery.domain.order.entity.Order;
import com.sparta.delivery.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "p_review")
public class Review extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID review_id;

    @Column(nullable = false)
    private String comment;

    @Column(nullable = false)
    private int star;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //배송지 정보 연관관계 설정 필요
}
