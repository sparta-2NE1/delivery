package com.sparta.delivery.domain.ai.dto;

import com.sparta.delivery.domain.ai.entity.AiInfo;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AiRequestDto {

    @Size(max = 150, message = "질문은 150자 이내로 입력 가능합니다.")
    private String question;

    public AiInfo toEntity(String answer) {
        return AiInfo.builder()
                .question(this.question)
                .answer(answer)
                .build();
    }
}
