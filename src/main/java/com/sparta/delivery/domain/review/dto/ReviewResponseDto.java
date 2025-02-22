package com.sparta.delivery.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponseDto {
    private UUID reviewId;
    private String comment;
    private int star;
    private UUID orderId;
    private UUID userId;
    private UUID storeId;
}
