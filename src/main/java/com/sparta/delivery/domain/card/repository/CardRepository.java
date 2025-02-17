package com.sparta.delivery.domain.card.repository;

import com.sparta.delivery.domain.card.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID> {

    Optional<Card> findByCardIdAndDeletedAtIsNullAndUser_Username(UUID cardId, String username);


    Optional<Card> findByCardIdAndDeletedAtIsNull(UUID cardId);

    List<Card> findByUser_UsernameAndDeletedAtIsNull(String username);

}
