package com.sparta.delivery.domain.card.dto;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@Data
@AllArgsConstructor
public class RegistrationCardDto {
    private String cardCompany;
    private String cardName;
    private String cardNumber;
}
