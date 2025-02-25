package com.sparta.delivery.domain.user.enums;


import java.util.Arrays;

public enum UserRoles {
    ROLE_CUSTOMER("ROLE_CUSTOMER"),  // 일반 사용자 (고객)
    ROLE_OWNER("ROLE_OWNER"),        // 가게를 소유한 사용자
    ROLE_MANAGER("ROLE_MANAGER"),    // 매니저 권한
    ROLE_MASTER("ROLE_MASTER");      // 최고 관리자

    private final String role;

    // 생성자
    UserRoles(String role) {
        this.role = role;
    }

    // String을 받아 Enum 값으로 변환하는 정적 메서드
    public static UserRoles fromString(String role) {
        return Arrays.stream(UserRoles.values())
                .filter(userRole -> userRole.role.equals(role))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역할: " + role));
    }
}