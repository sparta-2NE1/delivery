package com.sparta.delivery.domain.review.controller;

import com.sparta.delivery.config.PageableConfig;
import com.sparta.delivery.config.auth.PrincipalDetails;
import com.sparta.delivery.domain.review.dto.ReviewRequestDto;
import com.sparta.delivery.domain.review.dto.ReviewResponseDto;
import com.sparta.delivery.domain.review.dto.ReviewUpdateRequestDto;
import com.sparta.delivery.domain.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    private final PageableConfig pageableConfig;

    @Operation(summary = "리뷰 등록")
    @PostMapping("")
    public ResponseEntity<?> createReview(@RequestBody ReviewRequestDto requestDto, @AuthenticationPrincipal PrincipalDetails userDetails) {
        reviewService.createReview(requestDto, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "유저 리뷰 조회")
    @GetMapping("/getUserReview")
    public ResponseEntity<?> getUserReview(@RequestParam(name = "page", required = false) Integer page,
                                           @RequestParam(name = "size", required = false) Integer  size,
                                           @RequestParam(name = "sortBy", required = false) String sortBy,
                                           @RequestParam(name = "orderBy") String orderBy,
                                           @AuthenticationPrincipal PrincipalDetails userDetails) {
        Pageable pageable = pageableConfig.createPageRequest(page, size, sortBy, orderBy);
        return ResponseEntity.status(HttpStatus.OK)
                .body(reviewService.getUserReview(userDetails.getUsername(), pageable));
    }

    @Operation(summary = "가게 리뷰 조회")
    @GetMapping("/getStoreReview/{storeId}")
    public ResponseEntity<?> getStoreReview(@RequestParam(name = "page", required = false) Integer page,
                                            @RequestParam(name = "size", required = false) Integer  size,
                                            @RequestParam(name = "sortBy", required = false) String sortBy,
                                            @RequestParam(name = "orderBy") String orderBy,
                                           @PathVariable("storeId") UUID storeId) {
        Pageable pageable = pageableConfig.createPageRequest(page, size, sortBy, orderBy);
        return ResponseEntity.status(HttpStatus.OK)
                .body(reviewService.getStoreReview(storeId, pageable));
    }

    @Operation(summary = "리뷰 삭제")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable("reviewId") UUID reviewId, @AuthenticationPrincipal PrincipalDetails userDetails) {
        reviewService.deleteReview(reviewId, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "리뷰 수정")
    @PutMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(@PathVariable("reviewId") UUID reviewId, @RequestBody ReviewUpdateRequestDto requestDto, @AuthenticationPrincipal PrincipalDetails userDetails) {
        reviewService.updateReview(reviewId, requestDto, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "가게 리뷰 검색 - 별점 기반")
    @GetMapping("/searchReview/{storeId}")
    public ResponseEntity<?> getStoreReviewSearch(@PageableDefault(size = 10, page = 0, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                           @PathVariable("storeId") UUID storeId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(reviewService.getStoreReviewSearch(storeId, pageable));
    }

}
