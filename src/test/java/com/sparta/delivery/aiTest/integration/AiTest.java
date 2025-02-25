//package com.sparta.delivery.aiTest.integration;
//
//import com.sparta.delivery.domain.user.enums.UserRoles;
//import com.sparta.delivery.util.JwtTestUtil;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.*;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class AiTest {
//
//    @Autowired
//    private JwtTestUtil jwtTestUtil;
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    private static final String BASE_URL = "/api/ai";
//
//    private static final String REQUEST_TEXT = "하와이안 피자 상품명 추천";
//
//    @Test
//    @DisplayName("AI 상품 설명 문구 추천 통합 테스트")
//    public void recommendText() throws Exception {
//        // Given: JWT Access Token 생성
//        String jwtAccessToken = jwtTestUtil.createJwt("TestUser", UserRoles.ROLE_MASTER);
//
//        // When: API 호출
//        ResultActions resultActions = mockMvc.perform(post(BASE_URL)
//                .header("Authorization", "Bearer " + jwtAccessToken)
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\"question\": \"" + REQUEST_TEXT + "\"}"))
//                .andDo(print());
//
//        // Then: HTTP Status 200 OK
//        resultActions.andExpect(status().isOk());
//    }
//}
