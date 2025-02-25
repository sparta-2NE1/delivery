package com.sparta.delivery.domain.product.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.*;

@Target({ElementType.METHOD}) // 메서드에만 적용할 수 있음
@Retention(RetentionPolicy.RUNTIME) // 런타임까지 유지됨
@Documented // javadoc 과 같은 문서에 포함되도록 지정
public @interface ProductSwaggerDocs {

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "상품 등록", description = "스토어에 새로운 상품을 등록합니다.")
    @Parameters({
            @Parameter(name = "storeId", description = "상품을 등록할 스토어의 UUID", example = "af2a560c-1512-4912-97ce-02f9afce72aa")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 등록 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
            @ApiResponse(responseCode = "403", description = "상품 등록 권한 없음"),
            @ApiResponse(responseCode = "404", description = "스토어가 존재하지 않거나 ID가 적절하지 않은 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @interface AddProductToStore {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "상품 상세 정보 조회", description = "상품 ID를 통해 상품 상세 정보를 조회합니다.")
    @Parameters({
            @Parameter(name = "productId", description = "조회할 상품의 UUID", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 상품이 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @interface GetProduct {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "상품 전체 조회", description = "모든 상품의 리스트를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "상품이 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @interface AllProductList {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "스토어 상품 조회", description = "특정 스토어의 상품 리스트를 조회합니다.")
    @Parameters({
            @Parameter(name = "storeId", description = "조회할 스토어의 UUID", example = "af2a560c-1512-4912-97ce-02f9afce72aa")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 스토어의 상품이 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @interface GetStoreProducts {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "상품 정보 수정", description = "상품의 정보를 수정합니다.")
    @Parameters({
            @Parameter(name = "productId", description = "수정할 상품의 UUID", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 정보 수정 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
            @ApiResponse(responseCode = "403", description = "수정 권한 없음"),
            @ApiResponse(responseCode = "404", description = "해당 상품이 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @interface UpdateProduct {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "상품 삭제", description = "상품을 삭제합니다.")
    @Parameters({
            @Parameter(name = "productId", description = "삭제할 상품의 UUID", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
            @ApiResponse(responseCode = "404", description = "해당 상품이 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @interface DeleteProduct {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "상품 검색", description = "키워드를 통해 상품을 검색합니다.")
    @Parameter(name = "productName", description = "검색할 상품명")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 성공"),
            @ApiResponse(responseCode = "404", description = "상품이 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @interface SearchProduct {}
}
