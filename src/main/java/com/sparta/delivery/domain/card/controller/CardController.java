package com.sparta.delivery.domain.card.controller;

import com.sparta.delivery.config.auth.PrincipalDetails;
import com.sparta.delivery.domain.card.dto.RegistrationCardDto;
import com.sparta.delivery.domain.card.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/card")
@Tag(name = "Card API")
public class CardController {

    private final CardService cardService;

    @Operation(summary = "결제 카드 등록")
    @PostMapping
    public ResponseEntity<?> registrationCard(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody RegistrationCardDto registrationCardDto) {
        cardService.registrationCard(principalDetails.getUsername(), registrationCardDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "카드 단일 조회")
    @GetMapping("/{card_id}")
    public ResponseEntity<?> getCard(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable UUID card_id) {
        return ResponseEntity.ok().body(cardService.getCard(principalDetails.getUsername(), card_id));
    }

    @Operation(summary = "카드 리스트 조회")
    @GetMapping("/cards")
    public ResponseEntity<?> getCards(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok().body(cardService.getCards(principalDetails.getUsername()));
    }

    // TODO : PATCH로 바꾸기
    @Operation(summary = "카드 정보 수정")
    @PatchMapping("/update/{card_id}")
    public ResponseEntity<?> updateCard(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable UUID card_id, @RequestBody RegistrationCardDto registrationCardDto) {
        cardService.updateCard(principalDetails.getUsername(), card_id, registrationCardDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "카드 삭제")
    @PatchMapping("/{card_id}")
    public ResponseEntity<?> deleteCard(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable UUID card_id) {
        cardService.deleteCard(principalDetails.getUsername(), card_id);
        return ResponseEntity.ok().build();
    }
}
