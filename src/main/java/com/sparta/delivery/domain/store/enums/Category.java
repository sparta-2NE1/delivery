package com.sparta.delivery.domain.store.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Category {
    한식, // 음식카테고리
    중식,
    분식,
    치킨,
    피자;

    @JsonCreator
    public static Category from(String value) {
        for (Category category : Category.values()) {
            if (category.name().equals(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("유효하지 않은 카테고리 값입니다. 허용된 값: [치킨, 한식, 중식, 분식, 피자]");
    }
}
