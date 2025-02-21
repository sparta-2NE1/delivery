package com.sparta.delivery.domain.user.swagger;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;

import java.lang.annotation.*;

@Target({ElementType.METHOD}) // 메서드에만 적용할 수 있음
@Retention(RetentionPolicy.RUNTIME) // 런타임까지 유지됨
@Documented // javadoc 과 같은 문서에 포함되도록 지정
public @interface SwaggerUserDocs {

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "회원가입", description = "회원가입 요청을 처리합니다.")
    @interface SignUp {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "로그인", description = "사용자 로그인 후 JWT를 발급합니다.")
    @interface SignIn {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "로그아웃", description = "사용자의 RefreshToken을 제거하고 로그아웃 처리합니다.")
    @interface Logout {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "회원 정보 조회", description = "유저 ID를 통해 회원 정보를 조회합니다.")
    @Parameters({
            @Parameter(name = "id", description = "조회할 회원의 UUID", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    })
    @interface GetUser {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "회원 목록 검색", description = "검색 조건을 기반으로 회원 목록을 조회합니다.")
    @interface SearchUsers {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "회원 정보 수정", description = "회원의 정보를 수정합니다.")
    @Parameters({
            @Parameter(name = "id", description = "수정할 회원의 UUID", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    })
    @interface UpdateUser {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "회원 권한 수정", description = "회원의 권한을 변경합니다.")
    @Parameters({
            @Parameter(name = "id", description = "권한을 수정할 회원의 UUID", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    })
    @interface UpdateRole {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "회원 삭제", description = "회원 정보를 논리적으로 삭제합니다.")
    @Parameters({
            @Parameter(name = "id", description = "삭제할 회원의 UUID", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    })
    @interface DeleteUser {}
}
