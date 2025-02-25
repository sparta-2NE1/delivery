package com.sparta.delivery.cardTest;

import com.sparta.delivery.config.global.exception.custom.ExistCardException;
import com.sparta.delivery.domain.card.dto.RegistrationCardDto;
import com.sparta.delivery.domain.card.entity.Card;
import com.sparta.delivery.domain.card.repository.CardRepository;
import com.sparta.delivery.domain.card.service.CardService;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.enums.UserRoles;
import com.sparta.delivery.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test") // application-test.yml을 기준으로 test 실행 환경을 구성합니다.
@SpringBootTest  // Spring 컨텍스트를 로드하여 통합 테스트 수행
@Transactional  // 각 테스트 후 롤백하여 데이터 정합성 유지
public class CardServiceIntegrationTest {
    @Autowired
    private CardService cardService;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private UUID cardId;
    private Card testCard;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(UUID.randomUUID())
                .email("test@example.com")
                .password(passwordEncoder.encode("password"))
                .username("testuser")
                .nickname("testnick")
                .role(UserRoles.ROLE_CUSTOMER)
                .build();

        testUser = userRepository.save(testUser);

        cardId = UUID.randomUUID();
        testCard = Card.builder()
                .cardId(cardId)
                .cardCompany("국민")
                .cardNumber("1234")
                .cardName("국민카드")
                .user(testUser)
                .build();
        testCard = cardRepository.save(testCard);
        cardId = testCard.getCardId();
    }

    @Test
    @DisplayName("카드 등록 성공")
    void testRegisterCardSuccess(){
        RegistrationCardDto registrationCardDto = new RegistrationCardDto("국민","카드이름","12345");
        int size = cardService.getCards(testUser.getUsername()).size();
        cardService.registrationCard(testUser.getUsername(), registrationCardDto);
        int size2 = cardService.getCards(testUser.getUsername()).size();
        assertEquals(size+1,size2);
    }

    @Test
    @DisplayName("카드 등록 실패 : 카드 정보 누락")
    void testRegisterCardFailIllegal(){
        RegistrationCardDto registrationCardDto = new RegistrationCardDto("국민","카드이름",null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cardService.registrationCard(testUser.getUsername(), registrationCardDto));
        assertEquals("필수 입력 값입니다.",exception.getMessage());
    }

    @Test
    @DisplayName("카드 등록 실패 : 이미 등록된 카드")
    void testRegisterCardFailAlreadyExists() {
        RegistrationCardDto dto = new RegistrationCardDto("국민", "국민카드", "1234");
        ExistCardException exception = assertThrows(ExistCardException.class, () -> cardService.registrationCard("testuser", dto));
        assertEquals("이미 등록한 카드입니다", exception.getMessage());
    }

    @Test
    @DisplayName("카드 단일 조회 성공")
    void testGetCardSuccess(){
        RegistrationCardDto registrationCardDto = cardService.getCard(testUser.getUsername(), cardId);
        assertNotNull(registrationCardDto);
    }

    @Test
    @DisplayName("카드 단일 조회 실패 : 존재하지 않는 카드")
    void testGetCardFailNotFound(){
        cardService.deleteCard(testUser.getUsername(), cardId);
        NullPointerException exception = assertThrows(NullPointerException.class, () -> cardService.getCard(testUser.getUsername(), cardId));
        assertEquals("해당 카드가 존재하지 않습니다.",exception.getMessage());
    }

    @Test
    @DisplayName("카드 정보 업데이트")
    void testUpdateCardSuccess() {
        cardService.updateCard(testUser.getUsername(),cardId,new RegistrationCardDto("신한",null,null));
        String cardCompany = cardService.getCard(testUser.getUsername(), cardId).getCardCompany();
        assertEquals(cardCompany,"신한");
    }
    @Test
    @DisplayName("카드 삭제 성공")
    void testDeleteCardSuccess() {
        cardService.deleteCard(testUser.getUsername(), cardId);
        NullPointerException exception = assertThrows(NullPointerException.class, () -> cardService.getCard(testUser.getUsername(), cardId));
        assertEquals("해당 카드가 존재하지 않습니다.",exception.getMessage());
    }
}
