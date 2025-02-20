package com.sparta.delivery.domain.review.dto;

import com.sparta.delivery.domain.order.entity.Order;
import com.sparta.delivery.domain.review.entity.Review;
import com.sparta.delivery.domain.user.entity.User;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ReviewRequestDto {
    private String comment;
    private int star;
    private UUID orderId;

    public Review toReview(Order order, User user) {
        return Review.builder()
                .comment(comment)
                .star(star)
                .order(order)
                .store(order.getStores())
                .user(user).build();
    }
}
