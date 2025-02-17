package com.sparta.delivery.domain.card.repository;

import com.sparta.delivery.domain.card.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID> {

}
