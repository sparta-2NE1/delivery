package com.sparta.delivery.domain.token.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.*;

@Target({ElementType.METHOD}) // 메서드에만 적용할 수 있음
@Retention(RetentionPolicy.RUNTIME) // 런타임까지 유지됨
@Documented // javadoc 과 같은 문서에 포함되도록 지정
public @interface TokenSwaggerDocs {

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "엑세스 토큰 재발급",
            description = "리프레시 토큰을 쿠키에서 가져와 새로운 엑세스  토큰을 발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "엑세스 토큰 재발급 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지않은 리프레시 토큰"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청 (쿠키 없음 또는 만료됨)"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameter(
            name = "refresh",
            description = "리프레시 토큰 (쿠키에서 전달)",
            required = true
    )
    @interface reissueAccessToken {}
}
