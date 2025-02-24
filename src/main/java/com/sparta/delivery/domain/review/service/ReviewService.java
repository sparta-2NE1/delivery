package com.sparta.delivery.domain.review.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.delivery.config.global.exception.custom.*;
import com.sparta.delivery.domain.order.entity.Order;
import com.sparta.delivery.domain.order.enums.OrderStatus;
import com.sparta.delivery.domain.order.repository.OrderRepository;
import com.sparta.delivery.domain.review.dto.ReviewRequestDto;
import com.sparta.delivery.domain.review.dto.ReviewResponseDto;
import com.sparta.delivery.domain.review.dto.ReviewUpdateRequestDto;
import com.sparta.delivery.domain.review.entity.QReview;
import com.sparta.delivery.domain.review.entity.Review;
import com.sparta.delivery.domain.review.repository.ReviewRepository;
import com.sparta.delivery.domain.store.entity.Stores;
import com.sparta.delivery.domain.store.repository.StoreRepository;
import com.sparta.delivery.domain.store.service.StoreService;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;

    private final StoreService storeService;

    private final int REVIEW_PLUS = 1;
    private final int REVIEW_MINUS = -1;
    private final int REVIEW_UPDATE = 0;

    public void createReview(ReviewRequestDto requestDto, String username) {
        try {
            User user = getUser(username);
            Order order = getOrder(requestDto.getOrderId(), user);
            Stores stores = getStores(order.getStores().getStoreId());

            if(!order.getOrderStatus().equals(OrderStatus.ORDER_COMPLETE)) {
                throw new ReviewNotAllowedException("주문이 모두 완료되었을 경우 리뷰 작성이 가능합니다.");
            }

            Review review = requestDto.toReview(order, user, stores);
            storeService.updateStoreReview(order.getStores().getStoreId(), review.getStar(), REVIEW_PLUS);
            reviewRepository.save(review);

        } catch (Exception e) {
            throw e;
        }
    }

    public Page<ReviewResponseDto> getUserReview(String username, Pageable pageable) {
        try {
            User user = getUser(username);
            Page<Review> reviewList = reviewRepository.findAllByUserAndDeletedAtIsNull(user, pageable);

            if(reviewList.isEmpty()) {
                throw new ReviewNotFoundException("로그인한 사용자가 작성한 리뷰가 존재하지 않습니다.");
            }

            return reviewList.map(Review::toResponseDto);
        } catch (Exception e) {
            throw e;
        }
    }

    public Page<ReviewResponseDto> getStoreReviewSearch(UUID storeId, List<Integer> starList, PageRequest pageable) {
        try {
            Stores store = getStores(storeId);

            QReview qReview = QReview.review;
            BooleanBuilder builder = new BooleanBuilder();
            builder.and(qReview.stores.eq(store));
            builder.and(qReview.deletedAt.isNull());

            if(!starList.isEmpty() && starList != null) {
                builder.and(qReview.star.in(starList));
            }

            Page<Review> reviewList = reviewRepository.findAll(builder, pageable);

            if(reviewList.isEmpty()) {
                if(starList.isEmpty())
                    throw new ReviewNotFoundException("해당 가게에 작성된 리뷰가 존재하지 않습니다.");

                throw new ReviewNotFoundException("해당 가게에 조건에 맞는 리뷰가 존재하지 않습니다.");
            }

            return reviewList.map(Review::toResponseDto);
        } catch (Exception e) {
            throw e;
        }
    }

    public void deleteReview(UUID reviewId, String username) {
        try {
            User user = getUser(username);
            Review review = getReview(reviewId, user);

            review.setDeletedAt(LocalDateTime.now());
            review.setDeletedBy(username);

            int deleteStar = review.getStar() * -1;
            storeService.updateStoreReview(review.getStores().getStoreId(), deleteStar, REVIEW_MINUS);

            reviewRepository.save(review);

        } catch (Exception e) {
            throw e;
        }
    }

    public ReviewResponseDto updateReview(UUID reviewId, ReviewUpdateRequestDto requestDto, String username) {
        try {
            User user = getUser(username);
            Review review = getReview(reviewId, user);

            //새 별점 - 기존 별점
            int updateStar = requestDto.getStar() - review.getStar();

            review.setComment(requestDto.getComment());
            review.setStar(requestDto.getStar());

            storeService.updateStoreReview(review.getStores().getStoreId(), updateStar, REVIEW_UPDATE);
            return reviewRepository.save(review).toResponseDto();

        } catch (Exception e) {
            throw e;
        }
    }

    private User getUser(String username) {
        return userRepository.findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않거나 탈퇴한 유저입니다."));
    }

    private Order getOrder(UUID orderId, User user) {
        return orderRepository.findByOrderIdAndUserAndDeletedAtIsNull(orderId, user)
                .orElseThrow(() -> new UserOrderNotFoundException("존재하지 않거나 현재 로그인한 사용자의 주문이 아닙니다."));
    }

    private Review getReview(UUID reviewId, User user) {
        return reviewRepository.findByReviewIdAndUserAndDeletedAtIsNull(reviewId, user)
                .orElseThrow(() -> new ReviewNotFoundException("존재하지 않거나 현재 로그인한 사용자의 리뷰가 아닙니다."));
    }

    private Stores getStores(UUID storeId) {
       return storeRepository.findByStoreIdAndDeletedAtIsNull(storeId)
               .orElseThrow(() -> new StoreNotFoundException("존재하지 않는 가게입니다."));
    }
}
