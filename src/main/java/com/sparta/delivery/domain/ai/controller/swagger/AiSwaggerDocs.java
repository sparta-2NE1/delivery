package com.sparta.delivery.domain.ai.controller.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.*;

@Target({ElementType.METHOD}) // 메서드에만 적용할 수 있음
@Retention(RetentionPolicy.RUNTIME) // 런타임까지 유지됨
@Documented // javadoc 과 같은 문서에 포함되도록 지정
public @interface AiSwaggerDocs {

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "AI 상품 설명 문구 추천", description = "AI 상품 설명 문구 추천을 요청합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "AI 텍스트 추천 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @interface RecommendText {}

}
