package com.sparta.delivery.domain.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.sparta.delivery.config.global.exception.custom.InvalidApiResponseException;
import com.sparta.delivery.domain.ai.dto.AiRequestDto;
import com.sparta.delivery.domain.ai.dto.AiResponseDto;
import com.sparta.delivery.domain.ai.entity.AiInfo;
import com.sparta.delivery.domain.ai.repository.AiRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class AiService {

    private final AiRepository aiRepository;
    private final WebClient webClient;

    @Value("${ai.apikey}")
    private String API_KEY;
    private static final String BASE_URL = "https://generativelanguage.googleapis.com";
    private static final String CONSTRAINT = "답변을 최대한 간결하게 50자 이하로";

    public AiService(AiRepository aiRepository, WebClient.Builder webClientBuilder) {
        this.aiRepository = aiRepository;
        this.webClient = webClientBuilder.baseUrl(BASE_URL).build();
    }

    public AiResponseDto recommendText(AiRequestDto aiRequestDto) {
        String userQuestion = aiRequestDto.getQuestion();
        String questionWithConstraints = userQuestion + CONSTRAINT;

        // AI API 요청 바디
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(Map.of("parts", List.of(Map.of("text", questionWithConstraints)))));

        CompletableFuture<AiResponseDto> completableFuture = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1beta/models/gemini-1.5-flash:generateContent")
                        .queryParam("key", API_KEY)
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(JsonNode.class) // JSON 전체를 받아서 처리
                .map(this::extractText)
                .toFuture();

        try {
            AiResponseDto completedAiResponseDto = completableFuture.get();
            AiInfo aiInfo = aiRequestDto.toEntity(completedAiResponseDto.getAnswer());
            aiRepository.save(aiInfo);
            return completedAiResponseDto;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("AI API 호출 비동기 작업을 기다리는 동안 스레드가 인터럽트로 인해 중단되었습니다.", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("AI API 호출 비동기 작업 실행 중 알 수 없는 오류가 발생했습니다.", e);
        }
    }

    private AiResponseDto extractText(JsonNode jsonNode) {
        try {
            // 응답 구조: candidates[0] → content → parts[0] → text
            JsonNode textNode = jsonNode
                    .path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text");

            if (textNode.isMissingNode() || textNode.asText().isEmpty()) {
                throw new InvalidApiResponseException("AI API 응답에서 text 필드의 값을 찾을 수 없습니다.");
            }
            return new AiResponseDto(textNode.asText());
        } catch (Exception e) {
            throw new RuntimeException("응답 파싱 중 알 수 없는 오류가 발생했습니다.");
        }
    }
}
