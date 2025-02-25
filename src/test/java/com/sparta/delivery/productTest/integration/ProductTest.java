package com.sparta.delivery.productTest.integration;

import com.sparta.delivery.util.JwtTestUtil;
import com.sparta.delivery.domain.user.enums.UserRoles;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductTest {

    @Autowired
    private JwtTestUtil jwtTestUtil;

    @Autowired
    private MockMvc mockMvc;

    private static final String BASE_URL = "/api/products";

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:postgresql://localhost:5432/delivery");
        registry.add("spring.datasource.username", () -> "twenty1");
        registry.add("spring.datasource.password", () -> "twenty1");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
        registry.add("spring.jwt.secret", () -> "test_secret_key");
        registry.add("spring.jwt.accessTokenValidityInMilliseconds", () -> 3600000);
        registry.add("spring.jwt.refreshTokenValidityInMilliseconds", () -> "86400000");
    }

    @Test
    @DisplayName("상품 리스트 조회 통합 테스트")
    public void getAllProducts() throws Exception {
        // Given: JWT Access Token 생성
        String jwtAccessToken = jwtTestUtil.createJwt("TestUser", UserRoles.ROLE_MASTER);

        // When: API 호출
        ResultActions resultActions = mockMvc.perform(get(BASE_URL)
                .header("Authorization", "Bearer " + jwtAccessToken)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        // Then: HTTP Status 200 OK
        resultActions.andExpect(status().isOk());
    }
}
