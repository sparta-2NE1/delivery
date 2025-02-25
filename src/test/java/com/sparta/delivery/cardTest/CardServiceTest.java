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
        when(userRepository.findByUsernameAndDeletedAtIsNull("testuser")).thenReturn(Optional.of(testUser));
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);

        assertDoesNotThrow(() -> cardService.registrationCard("testuser", registrationCardDto));
        verify(cardRepository, times(1)).save(any(Card.class));
    }

    @Test
    @DisplayName("카드 등록 실패 : 카드 정보 누락")
    void testRegisterCardFailIllegal(){
        RegistrationCardDto registrationCardDto = new RegistrationCardDto("국민","카드이름",null);
        when(userRepository.findByUsernameAndDeletedAtIsNull("testuser")).thenReturn(Optional.of(testUser));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cardService.registrationCard("testuser", registrationCardDto));
        assertEquals("필수 입력 값입니다.",exception.getMessage());
    }

    @Test
    @DisplayName("카드 등록 실패 : 이미 등록된 카드")
    void testRegisterCardFailAlreadyExists() {
        RegistrationCardDto dto = new RegistrationCardDto("국민", "국민카드", "1234");
        when(userRepository.findByUsernameAndDeletedAtIsNull("testuser")).thenReturn(Optional.of(testUser));
        when(cardRepository.findByUser_UsernameAndDeletedAtIsNull("testuser"))
                .thenReturn(List.of(testCard));

        ExistCardException exception = assertThrows(ExistCardException.class, () -> cardService.registrationCard("testuser", dto));
        assertEquals("이미 등록한 카드입니다", exception.getMessage());
    }

    @Test
    @DisplayName("카드 단일 조회 성공")
    void testGetCardSuccess(){
        when(userRepository.findByUsernameAndDeletedAtIsNull("testuser")).thenReturn(Optional.of(testUser));
        when(cardRepository.findByCardIdAndDeletedAtIsNullAndUser_Username(cardId, "testuser"))
                .thenReturn(Optional.of(testCard));
        RegistrationCardDto registrationCardDto = cardService.getCard("testuser",cardId);
        assertNotNull(registrationCardDto);
        assertAll(
                () -> assertEquals("국민", registrationCardDto.getCardCompany()),
                () -> assertEquals("1234", registrationCardDto.getCardNumber())
        );
    }

    @Test
    @DisplayName("카드 단일 조회 실패 : 존재하지 않는 카드")
    void testGetCardFailNotFound(){
        when(userRepository.findByUsernameAndDeletedAtIsNull("testuser")).thenReturn(Optional.of(testUser));
        when(cardRepository.findByCardIdAndDeletedAtIsNullAndUser_Username(cardId,"testuser"))
                .thenReturn(Optional.empty());
        NullPointerException exception = assertThrows(NullPointerException.class, () -> cardService.getCard("testuser", cardId));
        assertEquals("해당 카드가 존재하지 않습니다.",exception.getMessage());
    }

    @Test
    @DisplayName("카드 리스트 조회")
    void testGetCardsSuccess(){
        when(userRepository.findByUsernameAndDeletedAtIsNull("testuser")).thenReturn(Optional.of(testUser));
        when(cardRepository.findByUser_UsernameAndDeletedAtIsNull("testuser"))
                .thenReturn(List.of(testCard));
        List<RegistrationCardDto> list = cardService.getCards("testuser");
        assertNotNull(list);
        assertFalse(list.isEmpty());
    }

    @Test
    @DisplayName("카드 정보 업데이트")
    void testUpdateCardSuccess() {
        when(userRepository.findByUsernameAndDeletedAtIsNull(testUser.getUsername())).thenReturn(Optional.of(testUser));
        when(cardRepository.findByCardIdAndDeletedAtIsNullAndUser_Username(cardId, "testuser"))
                .thenReturn(Optional.of(testCard));

        when(cardRepository.save(any(Card.class))).thenReturn(testCard.toBuilder()
                .cardCompany("신한")
                .cardNumber("5678")
                .cardName("신한카드")
                .build());

        assertDoesNotThrow(() -> cardService.updateCard("testuser", cardId, new RegistrationCardDto("nothing","nothing","nothing")));

        verify(cardRepository, times(1)).save(any(Card.class));
    }
    @Test
    @DisplayName("카드 삭제 성공")
    void testDeleteCardSuccess() {
        when(userRepository.findByUsernameAndDeletedAtIsNull("testuser")).thenReturn(Optional.of(testUser));
        when(cardRepository.findByCardIdAndDeletedAtIsNullAndUser_Username(cardId, "testuser"))
                .thenReturn(Optional.of(testCard));

        assertDoesNotThrow(() -> cardService.deleteCard("testuser", cardId));
        verify(cardRepository, times(1)).save(any(Card.class));
    }

    @Test
    @DisplayName("카드 삭제 실패 : 존재하지 않는 카드")
    void testDeleteCardFail_NotFound() {
        when(userRepository.findByUsernameAndDeletedAtIsNull("testuser")).thenReturn(Optional.of(testUser));
        when(cardRepository.findByCardIdAndDeletedAtIsNullAndUser_Username(cardId, "testuser"))
                .thenReturn(Optional.empty());

        NullPointerException exception = assertThrows(NullPointerException.class, () -> cardService.deleteCard("testuser", cardId));
        assertEquals("해당 카드가 존재하지 않습니다.", exception.getMessage());
    }


}