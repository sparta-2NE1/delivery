package com.sparta.delivery.domain.common;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
    @Column(nullable = false)
    private String updatedBy;

    // 레코드 삭제 일시
    @Column
    private LocalDateTime deletedAt;

    // 레코드 삭제 사용자
    @Column
    private String deletedBy;
}