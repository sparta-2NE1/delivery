package com.sparta.delivery.domain.delivery_address.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.*;

@Target({ElementType.METHOD}) // 메서드에만 적용할 수 있음
@Retention(RetentionPolicy.RUNTIME) // 런타임까지 유지됨
@Documented // javadoc 과 같은 문서에 포함되도록 지정
public @interface DeliveryAddressSwaggerDocs {


    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "배송지 등록", description = "배송지 등록 요청을 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배송지 등록 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameter(
            name = "Authorization",
            description = "새로운 엑세스 토큰 (응답 헤더에서 반환)",
            required = false
    )
    @interface addAddress {}
    
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "배송지 정보 조회", description = "배송지 ID를 통해 배송지 정보를 조회합니다.")
    @Parameters({
            @Parameter(name = "id", description = "조회할 배송지의 UUID", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameter(
            name = "Authorization",
            description = "새로운 엑세스 토큰 (응답 헤더에서 반환)",
            required = false
    )
    @interface getAddress {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "배송지 목록 검색", description = "검색 조건을 기반으로 배송지 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameter(
            name = "Authorization",
            description = "새로운 엑세스 토큰 (응답 헤더에서 반환)",
            required = false
    )
    @interface SearchAddress {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "배송지 정보 수정", description = "배송지의 정보를 수정합니다.")
    @Parameters({
            @Parameter(name = "id", description = "수정할 배송지의 UUID", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배송지 정보 수정 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
            @ApiResponse(responseCode = "403", description = "수정 권한 없음"),
            @ApiResponse(responseCode = "404", description = "해당 회원 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameter(
            name = "Authorization",
            description = "새로운 엑세스 토큰 (응답 헤더에서 반환)",
            required = false
    )
    @interface UpdateAddress {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "배송지 삭제", description = "배송지 정보를 논리적으로 삭제합니다.")
    @Parameters({
            @Parameter(name = "id", description = "삭제할 배송지의 UUID", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
            @ApiResponse(responseCode = "404", description = "회원 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameter(
            name = "Authorization",
            description = "새로운 엑세스 토큰 (응답 헤더에서 반환)",
            required = false
    )
    @interface DeleteAddress {}
}
