package com.sparta.delivery.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserReqDto {

    private String email;

    private String password;

    private String nickname;
}
