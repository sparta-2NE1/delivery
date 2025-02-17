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
public class UserUpdateReqDto {

    // 현재 비밀번호 (기존 비밀번호 확인용)
    @NotBlank(message = "Current password is required")
    private String currentPassword;

    // 비밀번호 검증 (8~15자, 대소문자, 숫자, 특수문자 포함)
    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
            message = "Invalid password format: 최소 8자 이상, 15자 이하이며 대소문자, 숫자, 특수문자가 포함되어야 합니다.")
    private String newPassword;

    // 이메일 검증
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    // 닉네임
    @NotBlank(message = "nickname is required")
    private String nickname;

}
