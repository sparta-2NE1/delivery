package com.sparta.delivery.domain.review.controller;

import com.sparta.delivery.config.PageableConfig;
import com.sparta.delivery.config.auth.PrincipalDetails;
import com.sparta.delivery.domain.review.dto.ReviewRequestDto;
import com.sparta.delivery.domain.review.dto.ReviewUpdateRequestDto;
import com.sparta.delivery.domain.review.service.ReviewService;
import com.sparta.delivery.domain.review.swagger.ReviewSwaggerDocs;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review")
public class ReviewController {
    private final ReviewService reviewService;

    private final PageableConfig pageableConfig;

    @ReviewSwaggerDocs.addReview
    @Operation(summary = "리뷰 등록")
    @PostMapping("")
    public ResponseEntity<?> createReview(@Valid @RequestBody ReviewRequestDto requestDto, @AuthenticationPrincipal PrincipalDetails userDetails) {
        reviewService.createReview(requestDto, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ReviewSwaggerDocs.getUserReview
    @Operation(summary = "유저 리뷰 조회")
    @GetMapping("/getUserReview")
    public ResponseEntity<?> getUserReview(@RequestParam(name = "page", required = false) Integer page,
                                           @RequestParam(name = "size", required = false) Integer size,
                                           @RequestParam(name = "sortBy", required = false) String sortBy,
                                           @RequestParam(name = "orderBy", required = false) String orderBy,
                                           @AuthenticationPrincipal PrincipalDetails userDetails) {
        Pageable pageable = pageableConfig.createPageRequest(page, size, sortBy, orderBy);
        return ResponseEntity.status(HttpStatus.OK)
                .body(reviewService.getUserReview(userDetails.getUsername(), pageable));
    }

    @ReviewSwaggerDocs.getStoreReview
    @Operation(summary = "가게 리뷰 검색 - 별점 기반 & 리뷰 전체 조회")
    @GetMapping("/getStoreReview/{storeId}")
    public ResponseEntity<?> getStoreReviewSearch(@RequestParam(name = "starList", required = false) List<Integer> starList,
                                                  @RequestParam(name = "page", required = false) Integer page,
                                                  @RequestParam(name = "size", required = false) Integer  size,
                                                  @RequestParam(name = "sortBy", required = false) String sortBy,
                                                  @RequestParam(name = "orderBy", required = false) String orderBy,
                                                  @PathVariable("storeId") UUID storeId) {
        //별점 안줄 시 전체 리뷰 리스트 return
        if (starList == null)
            starList = Collections.emptyList();

        PageRequest pageable = pageableConfig.createPageRequest(page, size, sortBy, orderBy);
        return ResponseEntity.status(HttpStatus.OK)
                .body(reviewService.getStoreReviewSearch(storeId, starList, pageable));
    }

    @ReviewSwaggerDocs.deleteReview
    @Operation(summary = "리뷰 삭제")
    @PatchMapping("/deleteReview/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable("reviewId") UUID reviewId, @AuthenticationPrincipal PrincipalDetails userDetails) {
        reviewService.deleteReview(reviewId, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ReviewSwaggerDocs.updateReview
    @Operation(summary = "리뷰 수정")
    @PatchMapping("/updateReview/{reviewId}")
    public ResponseEntity<?> updateReview(@PathVariable("reviewId") UUID reviewId, @RequestBody ReviewUpdateRequestDto requestDto, @AuthenticationPrincipal PrincipalDetails userDetails) {
        reviewService.updateReview(reviewId, requestDto, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
