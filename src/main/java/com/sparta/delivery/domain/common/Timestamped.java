package com.sparta.delivery.domain.common;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Timestamped {

    // 레코드 생성 일시
    @CreatedDate
    @Column(updatable = false , nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    // 레코드 생성자
    @Column(updatable = false , nullable = false)
    private String createdBy;

    // 레코드 수정 일시
    @LastModifiedDate
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;

    // 레코드 수정자
    @Column
    private String updatedBy;

    // 레코드 삭제 일시
    @Column
    private LocalDateTime deletedAt;

    // 레코드 삭제 사용자
    @Column
    private String deletedBy;

    // 생성 전 실행되는 로직
    @PrePersist
    protected void onCreate() {
        String currentUser = getCurrentUser(); // 현재 사용자 가져오기
        this.createdBy = currentUser;
        this.updatedBy = currentUser; // 생성 시 updatedBy도 같이 설정
    }

    // 업데이트 전 실행되는 로직
    @PreUpdate
    protected void onUpdate() {
        this.updatedBy = getCurrentUser(); // 업데이트 시 사용자 변경
    }

    // 현재 검증된 사용자 username을 가져오는 로직
    private String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {

                return ((UserDetails) principal).getUsername(); // 현재 로그인한 사용자의 username 반환
            } else {

                // 인증되지않은 사용자는 Security에서 anonymousUser 로 처리해 유저 createAt 이 anonymousUser 로 들어가
                // 2NE1 으로 설정
                return "2NE1";
            }
        }
        return "system"; // 기본값 (비로그인 상태)
    }
}