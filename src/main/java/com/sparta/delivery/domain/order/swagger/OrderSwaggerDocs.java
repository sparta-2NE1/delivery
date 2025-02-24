package com.sparta.delivery.domain.order.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.*;

@Target({ElementType.METHOD}) // 메서드에만 적용할 수 있음
@Retention(RetentionPolicy.RUNTIME) // 런타임까지 유지됨
@Documented // javadoc 과 같은 문서에 포함되도록 지정
public @interface OrderSwaggerDocs {

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "주문 등록", description = "주문 등록을 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 등록 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
            @ApiResponse(responseCode = "403", description = "주문 요청 상품 품절"),
            @ApiResponse(responseCode = "404", description = "올바르지 않은 가게, 배송지, 상품 ID"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameter(
            name = "Authorization",
            description = "새로운 엑세스 토큰 (응답 헤더에서 반환)",
            required = false
    )
    @interface addOrder {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "단일 주문 검색", description = "단일 주문을 검색합니다.")
    @Parameters({
            @Parameter(name = "orderId", description = "조회할 주문의 UUID", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 조회 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
            @ApiResponse(responseCode = "404", description = "올바르지 않은 주문 ID"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameter(
            name = "Authorization",
            description = "새로운 엑세스 토큰 (응답 헤더에서 반환)",
            required = false
    )
    @interface getOrder {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "유저 주문 검색", description = "검색 조건에 따라 유저의 주문을 검색합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 조회 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
            @ApiResponse(responseCode = "404", description = "주문이 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameter(
            name = "Authorization",
            description = "새로운 엑세스 토큰 (응답 헤더에서 반환)",
            required = false
    )
    @interface getUserOrder {}


    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "가게 주문 검색", description = "가게의 주문을 검색합니다.")
    @Parameters({
            @Parameter(name = "storeId", description = "주문을 조회할 가게의 UUID", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 조회 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
            @ApiResponse(responseCode = "403", description = "가게의 주인이 아님"),
            @ApiResponse(responseCode = "404", description = "주문이 존재하지 않거나 가게 ID가 잘못됨"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameter(
            name = "Authorization",
            description = "새로운 엑세스 토큰 (응답 헤더에서 반환)",
            required = false
    )
    @interface getStoreOrder {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "주문 삭제", description = "주문을 삭제합니다.")
    @Parameters({
            @Parameter(name = "orderId", description = "삭제할 주문의 UUID", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
            @ApiResponse(responseCode = "403", description = "주문 취소 가능시간이 지남"),
            @ApiResponse(responseCode = "404", description = "주문이나 상품 ID가 잘못됨"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameter(
            name = "Authorization",
            description = "새로운 엑세스 토큰 (응답 헤더에서 반환)",
            required = false
    )
    @interface deleteOrder {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "주문 수정", description = "주문을 수정합니다.")
    @Parameters({
            @Parameter(name = "orderId", description = "수정할 주문의 UUID", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 수정 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
            @ApiResponse(responseCode = "403", description = "주문 수정 가능시간이 지남"),
            @ApiResponse(responseCode = "404", description = "주문이나 상품 ID가 잘못됨"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameter(
            name = "Authorization",
            description = "새로운 엑세스 토큰 (응답 헤더에서 반환)",
            required = false
    )
    @interface updateOrder {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "주문 상태 수정", description = "주문을 수정합니다.")
    @Parameters({
            @Parameter(name = "orderId", description = "수정할 주문의 UUID", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 상태 수정 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
            @ApiResponse(responseCode = "404", description = "주문 ID가 잘못됨"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameter(
            name = "Authorization",
            description = "새로운 엑세스 토큰 (응답 헤더에서 반환)",
            required = false
    )
    @interface updateOrderStatus {}
}
