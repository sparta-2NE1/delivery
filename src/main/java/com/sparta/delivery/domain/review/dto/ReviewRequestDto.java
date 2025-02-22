package com.sparta.delivery.domain.review.dto;

import com.sparta.delivery.domain.order.entity.Order;
import com.sparta.delivery.domain.review.entity.Review;
import com.sparta.delivery.domain.store.entity.Stores;
import com.sparta.delivery.domain.user.entity.User;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ReviewRequestDto {
    @NotBlank(message = "리뷰 코멘트 작성은 필수입니다.")
    private String comment;

    @Min(value = 1, message = "별점은 최소 1점입니다.")
    @Max(value = 5, message = "별점은 최대 5점입니다.")
    private int star;

    @NotNull(message = "리뷰를 작성할 주문을 선택해주세요.")
    private UUID orderId;

    public Review toReview(Order order, User user, Stores stores) {
        return Review.builder()
                .comment(comment)
                .star(star)
                .order(order)
                .stores(stores)
                .user(user).build();
    }
}
