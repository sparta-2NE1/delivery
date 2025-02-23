package com.sparta.delivery.aiTest.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.delivery.domain.ai.dto.AiRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AiTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    private static final String baseUrl = "/api/ai";

    private static final String jwtAccessToken = "eyJhbGciOiJIUzI1NiJ9.eyJjYXRlZ29yeSI6ImFjY2VzcyIsInVzZXJuYW1lIjoic2Vvd29vNyIsImVtYWlsIjoic2Vvd29vQHRlc3QuY29tIiwicm9sZSI6IlJPTEVfQ1VTVE9NRVIiLCJpYXQiOjE3NDAyOTYwNTIsImlzcyI6IjJORTEiLCJleHAiOjE3NDAyOTk2NTJ9.P4ti-4jowEmioiADtR7aubJmEm3-oFo_2nsZZoH6dxk";

    private static final String requestText = "하와이안 피자 상품명 추천";

    @Test
    @DisplayName("AI 상품 설명 문구 추천 통합 테스트")
    public void recommendText() throws JsonProcessingException {
        AiRequestDto request = new AiRequestDto(requestText);

        // HTTP 요청 헤더에 Authorization 필드 추가
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtAccessToken);

        // HttpEntity를 사용하여 요청 헤더와 본문 함께 전달
        HttpEntity<AiRequestDto> requestEntity = new HttpEntity<>(request, headers);

        // POST 요청 수행
        ResponseEntity<String> response = testRestTemplate.exchange(baseUrl, HttpMethod.POST, requestEntity, String.class);

        // JSON 문자열을 ObjectMapper로 파싱
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        String answer = jsonNode.get("answer").asText();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(answer).isNotNull().isNotEmpty();
    }
}
