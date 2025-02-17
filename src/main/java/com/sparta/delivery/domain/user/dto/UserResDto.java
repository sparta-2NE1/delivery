package com.sparta.delivery.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserResDto {

    private UUID userId;

    private String username;

    private String email;

    private String nickname;

    private String role;

}
