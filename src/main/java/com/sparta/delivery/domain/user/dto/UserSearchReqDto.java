package com.sparta.delivery.domain.user.dto;

import com.sparta.delivery.domain.user.enums.UserRoles;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchReqDto {
    private int size = 10;         // 페이지 크기
    private int page = 0;          // 페이지 번호
    private String sortBy = "createdAt"; // 정렬 기준
    private String order = "desc"; // 정렬 방향 (asc 또는 desc)
    private String username;      // username 필터링
    private String email;         // email 필터링
    private UserRoles role;       // role 필터링
}
