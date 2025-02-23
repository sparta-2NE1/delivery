package com.sparta.delivery.domain.ai.controller;

import com.sparta.delivery.domain.ai.dto.AiRequestDto;
import com.sparta.delivery.domain.ai.dto.AiResponseDto;
import com.sparta.delivery.domain.ai.service.AiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;

    @PostMapping()
    public ResponseEntity<AiResponseDto> recommendText(@Valid @RequestBody AiRequestDto requestDto) {
        AiResponseDto aiResponseDto = aiService.recommendText(requestDto);
        return ResponseEntity.ok(aiResponseDto);
    }
}
