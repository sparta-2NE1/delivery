package com.sparta.delivery.domain.ai.entity;

import com.sparta.delivery.domain.common.Timestamped;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "p_ai_info")
public class AiInfo extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID aiId;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    private String answer;
}
