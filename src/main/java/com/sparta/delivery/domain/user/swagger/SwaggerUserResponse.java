package com.sparta.delivery.domain.user.swagger;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public @interface SwaggerUserResponse {

    @ApiResponses({
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @interface CommonResponse {}

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공, JWT 토큰 발급"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "409", description = "이미 로그인되어 있거나 비정상 로그아웃됨")
    })
    @interface SignInResponse {}

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 가입 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패")
    })
    @interface SignUpResponse {}

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "400", description = "쿠키에서 RefreshToken을 찾을 수 없음"),
            @ApiResponse(responseCode = "401", description = "잘못된 토큰이 전달됨"),
            @ApiResponse(responseCode = "409", description = "이미 로그아웃된 상태")
    })
    @interface LogoutResponse {}


    @ApiResponses({
            @ApiResponse(responseCode = "404" , description = "리소스를 찾을 수 없음")
    })
    @interface FindResponse{}


    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 정보 수정 성공"),
            @ApiResponse(responseCode = "403", description = "수정 권한 없음"),
            @ApiResponse(responseCode = "404", description = "해당 회원 없음")
    })
    @interface UpdateUserResponse {}

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
            @ApiResponse(responseCode = "404", description = "회원 없음")
    })
    @interface DeleteUserResponse {}
}
