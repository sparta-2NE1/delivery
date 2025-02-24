package com.sparta.delivery.domain.region.swagger;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.*;

@Target({ElementType.METHOD}) // 메서드에만 적용할 수 있음
@Retention(RetentionPolicy.RUNTIME) // 런타임까지 유지됨
@Documented // javadoc 과 같은 문서에 포함되도록 지정
public @interface RegionSwaggerDocs {

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "운영 지역 등록", description = "새로운 운영 지역을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "운영 지역 등록 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
            @ApiResponse(responseCode = "403", description = "지역 등록 권한 없음"),
            @ApiResponse(responseCode = "404", description = "가게가 존재하지 않거나 ID가 적절하지않은값"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @interface Register {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "특정 가게 운영 지역 리스트 조회", description = "특정 가게의 운영 지역 리스트를 조회합니다.")
    @Parameter(name = "storeId", description = "가게의 UUID", example = "af2a560c-1512-4912-97ce-02f9afce72aa")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않은 정보(가게 혹은 지역)"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @interface RegionList {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "운영 지역 전체 조회", description = "모든 운영 지역의 리스트를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "지역이 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @interface AllRegionList {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "운영 지역 검색", description = "키워드를 통해 운영 지역을 검색합니다.")
    @Parameter(name = "keyword", description = "검색 키워드")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 성공"),
            @ApiResponse(responseCode = "404", description = "지역이 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @interface Search {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "운영 지역 수정", description = "운영 지역 정보를 수정합니다.")
    @Parameter(name = "regionId", description = "수정할 운영 지역의 UUID", example = "af2a560c-1512-4912-97ce-02f9afce72aa")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 동주소:도렴동, 적선동, 중학동"),
            @ApiResponse(responseCode = "403", description = "지역 수정 권한 없음"),
            @ApiResponse(responseCode = "404", description = "지역이 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @interface Update {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "운영 지역 삭제", description = "운영 지역을 삭제합니다.")
    @Parameter(name = "regionId", description = "삭제할 운영 지역의 UUID", example = "af2a560c-1512-4912-97ce-02f9afce72aa")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "지역이 존재하지 않음"),
            @ApiResponse(responseCode = "403", description = "지역 삭제 권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @interface Delete {}

}
