package com.sparta.delivery.domain.token.repository;

import com.sparta.delivery.domain.token.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken , UUID> {
}
