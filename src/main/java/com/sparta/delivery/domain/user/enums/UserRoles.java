package com.sparta.delivery.domain.user.enums;


public enum UserRoles {
    ROLE_CUSTOMER("ROLE_CUSTOMER"),  // 일반 사용자 (고객)
    ROLE_OWNER("ROLE_OWNER"),        // 자원을 소유한 사용자
    ROLE_MANAGER("ROLE_MANAGER"),    // 매니저 권한
    ROLE_MASTER("ROLE_MASTER");      // 최고 관리자

    private final String role;

    // 생성자
    UserRoles(String role) {
        this.role = role;
    }


}