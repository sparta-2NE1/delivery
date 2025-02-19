package com.sparta.delivery.cardTest;

import com.sparta.delivery.config.global.exception.custom.ExistCardException;
import com.sparta.delivery.domain.card.dto.RegistrationCardDto;
import com.sparta.delivery.domain.card.entity.Card;
import com.sparta.delivery.domain.card.repository.CardRepository;
import com.sparta.delivery.domain.card.service.CardService;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CardServiceTest {

    @InjectMocks
    private CardService cardService;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    private User testUser;
    private UUID cardId;
    private Card testCard;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = User.builder()
                .userId(UUID.randomUUID())
                .username("testuser")
                .build();

        cardId = UUID.randomUUID();
        testCard = Card.builder()
                .cardId(cardId)
                .cardCompany("국민")
                .cardNumber("1234")
                .cardName("국민카드")
                .user(testUser)
                .build();
    }

    @Test
    @DisplayName("카드 등록 성공")
    void testRegisterCardSuccess(){
        RegistrationCardDto registrationCardDto = new RegistrationCardDto("국민","카드이름","1234");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(cardRepository.save(any(Card.class))).thenReturn(Card.builder()
                .cardCompany("국민")
                .cardNumber("1234")
                .cardName("카드이름")
                .user(testUser)
                .build());

        assertDoesNotThrow(() -> cardService.registrationCard("testuser", registrationCardDto));
        verify(cardRepository, times(1)).save(any(Card.class));
    }
    @Test
    @DisplayName("카드 등록 실패 : 카드 정보 누락")
    void testRegisterCardFailIllegal(){
        RegistrationCardDto registrationCardDto = new RegistrationCardDto("국민","카드이름",null);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cardService.registrationCard("testuser", registrationCardDto));
        assertEquals("필수 입력 값입니다.",exception.getMessage());
    }


}