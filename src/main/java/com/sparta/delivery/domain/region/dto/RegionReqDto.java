package com.sparta.delivery.domain.region.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class RegionReqDto {

    private UUID storeId;

    @NotBlank(message = "도를 입력 해주세요")
    private String province;

    @NotBlank(message = "시를 입력 해주세요")
    private String city;

    @NotBlank(message = "동을 입력 해주세요")
    private String locality;

}
