package com.sparta.delivery.domain.review.controller;

import com.sparta.delivery.config.auth.PrincipalDetails;
import com.sparta.delivery.domain.review.dto.ReviewRequestDto;
import com.sparta.delivery.domain.review.dto.ReviewResponseDto;
import com.sparta.delivery.domain.review.dto.ReviewUpdateRequestDto;
import com.sparta.delivery.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("")
    public ResponseEntity<?> createReview(@RequestBody ReviewRequestDto requestDto, @AuthenticationPrincipal PrincipalDetails userDetails) {
        reviewService.createReview(requestDto, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<?> getReview(@PathVariable("reviewId") UUID reviewId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(reviewService.getReview(reviewId));
    }

    @GetMapping("/getUserReview")
    public ResponseEntity<?> getUserReview(@PageableDefault(size = 10, page = 0, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                           @AuthenticationPrincipal PrincipalDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(reviewService.getUserReview(userDetails.getUsername(), pageable));
    }

//    @GetMapping("/getStoreReview/{storeId}")
//    public ResponseEntity<?> getStoreReview(@PageableDefault(size = 10, page = 0, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
//                                           @PathVariable("storeId") UUID storeId) {
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(reviewService.getStoreReview(storeId, pageable));
//    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable("reviewId") UUID reviewId, @AuthenticationPrincipal PrincipalDetails userDetails) {
        reviewService.deleteReview(reviewId, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(@PathVariable("reviewId") UUID reviewId, @RequestBody ReviewUpdateRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(reviewService.updateReview(reviewId, requestDto));
    }

}
