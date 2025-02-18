package com.sparta.delivery.domain.card.service;

import com.sparta.delivery.config.global.exception.custom.ExistCardException;
import com.sparta.delivery.domain.card.dto.RegistrationCardDto;
import com.sparta.delivery.domain.card.entity.Card;
import com.sparta.delivery.domain.card.repository.CardRepository;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;


    private boolean existCard(String username,RegistrationCardDto registrationCardDto) {
        List<Card> cards = cardRepository.findByUser_UsernameAndDeletedAtIsNull(username);
        for(Card card : cards){
            if(card.getCardCompany().equals(registrationCardDto.getCardCompany())&&
                    card.getCardNumber().equals(registrationCardDto.getCardNumber())){
                return true;
            }
        }
        return false;
    }

    @Transactional
    public void registrationCard(String username, RegistrationCardDto registrationCardDto) {

        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new NullPointerException("유저가 존재하지 않습니다."));
        if(existCard(username,registrationCardDto)){
            throw new ExistCardException("이미 등록한 카드입니다");
        }

        Card card = Card.builder()
                .cardCompany(registrationCardDto.getCardCompany())
                .cardNumber(registrationCardDto.getCardNumber())
                .cardName(registrationCardDto.getCardName())
                .user(user)
                .build();
        cardRepository.save(card);
    }

    public RegistrationCardDto getCard(String username, UUID cardId) {
        Card card = cardRepository.findByCardIdAndDeletedAtIsNullAndUser_Username(cardId, username).orElseThrow(() ->
                new NullPointerException("카드가 존재하지 않습니다."));
        return RegistrationCardDto.builder()
                .cardCompany(card.getCardCompany())
                .cardName(card.getCardName())
                .cardNumber(card.getCardNumber())
                .build();
    }

    public List<RegistrationCardDto> getCards(String username) {
        List<Card> cards = cardRepository.findByUser_UsernameAndDeletedAtIsNull(username);
        return cards.stream().map(card -> RegistrationCardDto.builder()
                .cardNumber(card.getCardNumber())
                .cardName(card.getCardName())
                .cardCompany(card.getCardCompany())
                .build()).toList();
    }

    @Transactional
    public void updateCard(String username, UUID cardId, RegistrationCardDto registrationCardDto) {
        Card card = cardRepository.findByCardIdAndDeletedAtIsNullAndUser_Username(cardId, username).orElseThrow(() ->
                new NullPointerException("해당 카드가 존재하지 않습니다."));

        if(existCard(username,registrationCardDto)){
            throw new ExistCardException("이미 등록한 카드입니다");
        }

        card = card.toBuilder()
                .cardCompany(registrationCardDto.getCardCompany() != null ? registrationCardDto.getCardCompany() : card.getCardCompany())
                .cardNumber(registrationCardDto.getCardNumber() != null ? registrationCardDto.getCardNumber() : card.getCardNumber())
                .cardName(registrationCardDto.getCardName() != null ? registrationCardDto.getCardName() : card.getCardName())
                .build();
        cardRepository.save(card);
    }

    public void deleteCard(String username, UUID cardId) {
        Card card = cardRepository.findByCardIdAndDeletedAtIsNullAndUser_Username(cardId, username).orElseThrow(() ->
                new NullPointerException("해당 카드가 존재하지 않습니다."));
        card.setDeletedAt(LocalDateTime.now());
        card.setDeletedBy(username);
        cardRepository.save(card);


    }
}