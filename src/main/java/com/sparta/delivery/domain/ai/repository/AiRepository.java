package com.sparta.delivery.domain.ai.repository;

import com.sparta.delivery.domain.ai.entity.AiInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AiRepository extends JpaRepository<AiInfo, UUID> {

}
