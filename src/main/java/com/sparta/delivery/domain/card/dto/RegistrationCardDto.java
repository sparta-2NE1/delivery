package com.sparta.delivery.domain.card.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@Data
@AllArgsConstructor
public class RegistrationCardDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String cardCompany;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String cardName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String cardNumber;
}