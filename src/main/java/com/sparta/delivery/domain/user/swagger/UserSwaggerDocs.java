package com.sparta.delivery.domain.user.swagger;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.*;

@Target({ElementType.METHOD}) // 메서드에만 적용할 수 있음
@Retention(RetentionPolicy.RUNTIME) // 런타임까지 유지됨
@Documented // javadoc 과 같은 문서에 포함되도록 지정
public @interface UserSwaggerDocs {

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "회원가입", description = "회원가입 요청을 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 가입 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @interface SignUp {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "로그인", description = "사용자 로그인 후 JWT를 발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공, JWT 토큰 발급"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "409", description = "이미 로그인되어 있거나 비정상 로그아웃됨"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @interface SignIn {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "로그아웃", description = "사용자의 RefreshToken을 제거하고 로그아웃 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "400", description = "쿠키에서 RefreshToken을 찾을 수 없음"),
            @ApiResponse(responseCode = "401", description = "잘못된 토큰이 전달됨"),
            @ApiResponse(responseCode = "409", description = "이미 로그아웃된 상태"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameter(
            name = "refresh",
            description = "리프레시 토큰 (쿠키에서 전달)",
            required = true
    )
    @Parameter(
            name = "Authorization",
            description = "새로운 엑세스 토큰 (응답 헤더에서 반환)",
            required = false
    )
    @interface Logout {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "회원 정보 조회", description = "유저 ID를 통해 회원 정보를 조회합니다.")
    @Parameters({
            @Parameter(name = "id", description = "조회할 회원의 UUID", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
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
    @interface GetUser {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "회원 목록 검색", description = "검색 조건을 기반으로 회원 목록을 조회합니다.")
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
    @interface SearchUsers {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "회원 정보 수정", description = "회원의 정보를 수정합니다.")
    @Parameters({
            @Parameter(name = "id", description = "수정할 회원의 UUID", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 정보 수정 성공"),
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
    @interface UpdateUser {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "회원 권한 수정", description = "회원의 권한을 변경합니다.")
    @Parameters({
            @Parameter(name = "id", description = "권한을 수정할 회원의 UUID", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 정보 수정 성공"),
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
    @interface UpdateRole {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "회원 삭제", description = "회원 정보를 논리적으로 삭제합니다.")
    @Parameters({
            @Parameter(name = "id", description = "삭제할 회원의 UUID", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
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
    @interface DeleteUser {}
}
