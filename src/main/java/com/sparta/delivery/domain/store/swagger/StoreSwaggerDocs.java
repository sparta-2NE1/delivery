package com.sparta.delivery.domain.store.swagger;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.*;

@Target({ElementType.METHOD}) // 메서드에만 적용할 수 있음
@Retention(RetentionPolicy.RUNTIME) // 런타임까지 유지됨
@Documented // javadoc 과 같은 문서에 포함되도록 지정
public @interface StoreSwaggerDocs {

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "가게 등록", description = "새로운 가게를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가게 등록 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @interface Register {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "가게 리스트 조회", description = "모든 가게의 리스트를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "가게가 등록되어 있지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @interface StoreList {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "가게 단일 조회", description = "특정 가게의 정보를 조회합니다.")
    @Parameter(name = "storeId", description = "조회할 가게의 UUID", required = true, example = "af2a560c-1512-4912-97ce-02f9afce72aa")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 가게가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @interface StoreOne {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "가게 검색", description = "키워드와 카테고리로 가게를 검색합니다.")
    @Parameters({
            @Parameter(name = "keyword", description = "검색 키워드"),
            @Parameter(name = "category", description = "검색할 카테고리")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 카테고리가 입력:한식,중식,분식,치킨,피자"),
            @ApiResponse(responseCode = "404", description = "가게결과가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @interface StoreSearch {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "가게 업데이트", description = "가게 정보를 수정합니다.")
    @Parameter(name = "storeId", description = "수정할 가게의 UUID", example = "af2a560c-1512-4912-97ce-02f9afce72aa")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
            @ApiResponse(responseCode = "404", description = "수정할 대상 가게가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @interface StoreUpdate {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "가게 삭제", description = "가게를 논리적으로 삭제합니다.")
    @Parameter(name = "storeId", description = "삭제할 가게의 UUID", example = "af2a560c-1512-4912-97ce-02f9afce72aa")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "삭제할 대상 가게가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @interface StoreDelete {}





}
