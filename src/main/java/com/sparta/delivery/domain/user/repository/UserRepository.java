package com.sparta.delivery.domain.user.repository;

import com.sparta.delivery.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User , UUID> {

    Optional<User> findByUsername(String username);

    // username 존재 유무 확인 (존재 : true , 없을 시 : false)
    boolean existsByUsername(String username);

    // 삭제되지 않은 유저 단일 조회
    Optional<User> findByUserIdAndDeletedAtIsNull(UUID id);

    // 삭제되지 않은 유저 페이징 조회 (생성일 최근일 부터 정렬)
    @Query("SELECT u FROM User u WHERE u.deletedAt IS NULL ORDER BY u.createdAt DESC")
    Page<User> findAllDeletedIsNull(Pageable pageable);
}
