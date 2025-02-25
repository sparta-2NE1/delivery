package com.sparta.delivery.domain.ai.controller;

import com.sparta.delivery.domain.ai.controller.swagger.AiSwaggerDocs;
import com.sparta.delivery.domain.ai.dto.AiRequestDto;
import com.sparta.delivery.domain.ai.dto.AiResponseDto;
import com.sparta.delivery.domain.ai.service.AiService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name ="AI API", description = "AI 상품 설명 문구 추천 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;

    @AiSwaggerDocs.RecommendText
    @PostMapping()
    public ResponseEntity<AiResponseDto> recommendText(@Valid @RequestBody AiRequestDto requestDto) {
        AiResponseDto aiResponseDto = aiService.recommendText(requestDto);
        return ResponseEntity.ok(aiResponseDto);
    }
}
