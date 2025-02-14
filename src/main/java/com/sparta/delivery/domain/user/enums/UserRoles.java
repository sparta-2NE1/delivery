package com.sparta.delivery.domain.user.enums;


public enum UserRoles {
    ROLE_ADMIN("ROLE_ADMIN"),      // 관리자 권한
    ROLE_MANAGER("ROLE_MANAGER"),  // 매니저 권한
    ROLE_USER("ROLE_USER");        // 일반 사용자 권한

    private final String role;

    // 생성자
    UserRoles(String role) {
        this.role = role;
    }

    // 역할 반환 메서드
    public String getRole() {
        return role;
    }

    // 문자열로 역할을 찾는 메서드
    public static UserRoles fromString(String role) {
        for (UserRoles userRole : UserRoles.values()) {
            if (userRole.getRole().equals(role)) {
                return userRole;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + role);
    }
}