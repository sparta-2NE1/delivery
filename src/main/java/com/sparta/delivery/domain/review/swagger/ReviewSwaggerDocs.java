package com.sparta.delivery.domain.review.swagger;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.*;

@Target({ElementType.METHOD}) // 메서드에만 적용할 수 있음
@Retention(RetentionPolicy.RUNTIME) // 런타임까지 유지됨
@Documented // javadoc 과 같은 문서에 포함되도록 지정
public @interface ReviewSwaggerDocs {

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "리뷰 등록", description = "리뷰 등록을 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 등록 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
            @ApiResponse(responseCode = "403", description = "리뷰 작성이 허용되지 않는 주문"),
            @ApiResponse(responseCode = "404", description = "올바르지 않은 주문 ID"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameter(
            name = "Authorization",
            description = "새로운 엑세스 토큰 (응답 헤더에서 반환)",
            required = false
    )
    @interface addReview {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "유저 리뷰 조회", description = "유저 리뷰를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 조회 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
            @ApiResponse(responseCode = "404", description = "리뷰가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameter(
            name = "Authorization",
            description = "새로운 엑세스 토큰 (응답 헤더에서 반환)",
            required = false
    )
    @interface getUserReview {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "가게 리뷰 검색", description = "검색 조건에 따라 가게 리뷰를 검색합니다.")
    @Parameters({
            @Parameter(name = "storeId", description = "조회할 가게의 UUID", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 조회 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
            @ApiResponse(responseCode = "404", description = "리뷰가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameter(
            name = "Authorization",
            description = "새로운 엑세스 토큰 (응답 헤더에서 반환)",
            required = false
    )
    @interface getStoreReview {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "리뷰 삭제", description = "작성된 리뷰를 삭제합니다.")
    @Parameters({
            @Parameter(name = "reviewId", description = "삭제할 리뷰의 UUID", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
            @ApiResponse(responseCode = "404", description = "리뷰가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameter(
            name = "Authorization",
            description = "새로운 엑세스 토큰 (응답 헤더에서 반환)",
            required = false
    )
    @interface deleteReview {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "리뷰 수정", description = "작성된 리뷰를 수정합니다.")
    @Parameters({
            @Parameter(name = "reviewId", description = "수정할 리뷰의 UUID", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
            @ApiResponse(responseCode = "404", description = "리뷰가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameter(
            name = "Authorization",
            description = "새로운 엑세스 토큰 (응답 헤더에서 반환)",
            required = false
    )
    @interface updateReview {}

}
