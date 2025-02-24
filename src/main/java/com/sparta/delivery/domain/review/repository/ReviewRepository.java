package com.sparta.delivery.domain.review.repository;

import com.sparta.delivery.domain.review.entity.Review;
import com.sparta.delivery.domain.store.entity.Stores;
import com.sparta.delivery.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID>, QuerydslPredicateExecutor<Review> {
    Optional<Review> findByReviewIdAndUserAndDeletedAtIsNull(UUID reviewId, User user);

    Page<Review> findAllByUserAndDeletedAtIsNull(User user, Pageable pageable);

    Page<Review> findAllByStoresAndDeletedAtIsNull(Stores store, Pageable pageable);
}
