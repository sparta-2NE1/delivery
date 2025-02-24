package com.sparta.delivery.domain.store.dto;

import com.sparta.delivery.domain.store.enums.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class StoreReqDto {


    @NotBlank(message = "이름을 넣어주셔야합니다!")
    private String name;

    @NotNull(message = "카테고리를 넣어주셔야합니다!")
    private Category category;

    @NotBlank(message = "주소를 넣어주셔야합니다!")
    private String address;

}
