package com.sparta.delivery.domain.review.service;

import com.sparta.delivery.domain.order.entity.Order;
import com.sparta.delivery.domain.order.repository.OrderRepository;
import com.sparta.delivery.domain.review.dto.ReviewRequestDto;
import com.sparta.delivery.domain.review.dto.ReviewResponseDto;
import com.sparta.delivery.domain.review.dto.ReviewUpdateRequestDto;
import com.sparta.delivery.domain.review.entity.Review;
import com.sparta.delivery.domain.review.repository.ReviewRepository;
import com.sparta.delivery.domain.store.entity.Stores;
import com.sparta.delivery.domain.store.repository.StoreRepository;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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

    public void createReview(ReviewRequestDto requestDto, String username) {
        try {
            User user = getUser(username);
            Order order = getOrder(requestDto.getOrderId());
            Review review = requestDto.toReview(order, user);
            reviewRepository.save(review);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        catch (Exception e) {
            throw new RuntimeException("리뷰 등록 중 알 수 없는 오류가 발생했습니다.");
        }
    }

    public ReviewResponseDto getReview(UUID reviewId) {
        try {
            Review review = reviewRepository.findByReviewIdAndDeletedAtIsNull(reviewId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다."));

            return review.toResponseDto();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("리뷰 조회 중 알 수 없는 오류가 발생했습니다.");
        }
    }

    public List<ReviewResponseDto> getUserReview(String username, Pageable pageable) {
        try {
            User user = getUser(username);
            List<Review> reviewList = reviewRepository.findAllByUserAndDeletedAtIsNull(user, pageable);

            if(reviewList.isEmpty()) {
                throw new IllegalArgumentException("해당 유저가 작성한 리뷰가 존재하지 않습니다.");
            }
            return reviewList.stream().map(review -> review.toResponseDto()).toList();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("유저 리뷰 조회 중 알 수 없는 오류가 발생했습니다.");
        }
    }

    public List<ReviewResponseDto> getStoreReview(UUID storeId, Pageable pageable) {
        try {
            Stores store = getStores(storeId);
            List<Review> reviewList = reviewRepository.findAllByStoreAndDeletedAtIsNull(store, pageable);

            if(reviewList.isEmpty()) {
                throw new IllegalArgumentException("해당 가게에 작성된 리뷰가 존재하지 않습니다.");
            }
            return reviewList.stream().map(review -> review.toResponseDto()).toList();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("가게 리뷰 조회 중 알 수 없는 오류가 발생했습니다.");
        }
    }

    public void deleteReview(UUID reviewId, String username) {
        try {
            Review review = reviewRepository.findByReviewIdAndDeletedAtIsNull(reviewId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다."));

            review.setDeletedAt(LocalDateTime.now());
            review.setDeletedBy(username);
            reviewRepository.save(review);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("리뷰 삭제 중 알 수 없는 오류가 발생했습니다.");
        }
    }

    public ReviewResponseDto updateReview(UUID reviewId, ReviewUpdateRequestDto requestDto) {
        try {
            Review review = reviewRepository.findByReviewIdAndDeletedAtIsNull(reviewId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다."));

            review.setComment(requestDto.getComment());
            review.setStar(requestDto.getStar());

            return reviewRepository.save(review).toResponseDto();

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("리뷰 수정 중 알 수 없는 오류가 발생했습니다.");
        }
    }

    private User getUser(String username) {
        return userRepository.findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 탈퇴한 유저입니다."));
    }

    private Order getOrder(UUID orderId) {
        return orderRepository.findByOrderIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 취소된 주문입니다."));
    }

    private Stores getStores(UUID storeId) {
       return storeRepository.findByStoreIdAndDeletedAtIsNull(storeId)
               .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 삭제된 가게입니다."));
    }
}
