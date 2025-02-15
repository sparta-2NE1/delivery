package com.sparta.delivery.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserReqDto {

    // 사용자 이름 검증 (4~10자의 소문자 + 숫자)
    @NotBlank(message = "Username is required")
    @Pattern(regexp = "^[a-z0-9]{4,10}$",
            message = "Invalid username format: 소문자(a~z), 숫자(0~9)로 4~10자여야 합니다.")
    private String username;

    // 비밀번호 검증 (8~15자, 대소문자, 숫자, 특수문자 포함)
    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
            message = "Invalid password format: 최소 8자 이상, 15자 이하이며 대소문자, 숫자, 특수문자가 포함되어야 합니다.")
    private String password;

    // 이메일 검증
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    // 닉네임
    @NotBlank(message = "nickname is required")
    private String nickname;
}
