package com.sparta.delivery.regionTest.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.delivery.domain.region.dto.RegionReqDto;
import com.sparta.delivery.domain.store.dto.StoreReqDto;
import com.sparta.delivery.domain.store.enums.Category;
import com.sparta.delivery.domain.user.dto.LoginRequestDto;
import com.sparta.delivery.domain.user.dto.SignupReqDto;
import com.sparta.delivery.domain.user.dto.UserRoleUpdateReqDto;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.enums.UserRoles;
import com.sparta.delivery.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class RegionServiceIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("권한을 가진 MASTER가 다른 유저의 권한 CUSTOMER -> OWNER로 변경 후, OWNER가 지역 등록 성공")
    void testUpdateRoleAndCreateRegion() throws Exception {
        // 1) "masterUser" 회원가입 (기본 ROLE_CUSTOMER), userId 반환
        String masterUsername = "masteruser";
        String masterPassword = "MasterPass123!";
        UUID masterUserId = signupUserAndGetId(masterUsername, masterPassword);
        assertThat(masterUserId).isNotNull();

        // (테스트 전용) DB 접근 -> masterUser를 ROLE_MASTER 로 변경
        forceChangeUserRole(masterUserId, UserRoles.ROLE_MASTER);

        // 2) masterUser 로그인 -> masterToken
        String masterToken = loginAndGetBearerToken(masterUsername, masterPassword);
        assertThat(masterToken).isNotNull();

        // 3) "ownerUser" 회원가입 (기본 ROLE_CUSTOMER)
        String ownerUsername = "owneruser";
        String ownerPassword = "OwnerPass123!";
        UUID ownerUserId = signupUserAndGetId(ownerUsername, ownerPassword);
        assertThat(ownerUserId).isNotNull();

        // 4) 마스터가 "ownerUser" -> ROLE_OWNER 변경
        UserRoleUpdateReqDto roleUpdateDto = new UserRoleUpdateReqDto(UserRoles.ROLE_OWNER);

        mockMvc.perform(patch("/api/user/" + ownerUserId + "/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, masterToken)
                        .content(objectMapper.writeValueAsString(roleUpdateDto)))
                .andExpect(status().isOk()); // MASTER만 가능

        // 5) "ownerUser" 로그인 -> 이제 ROLE_OWNER
        String ownerToken = loginAndGetBearerToken(ownerUsername, ownerPassword);

        // 6) OWNER 권한으로 스토어 생성
        UUID storeId = createTestStore(ownerToken);

        // 7) OWNER 권한으로 Region 생성
        RegionReqDto regionReqDto = new RegionReqDto();
        regionReqDto.setStoreId(storeId);
        regionReqDto.setProvince("도시");
        regionReqDto.setCity("서울");
        regionReqDto.setLocality("중학동");

        mockMvc.perform(post("/api/region")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, ownerToken)
                        .content(objectMapper.writeValueAsString(regionReqDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("유저가(CUSTOMER) OWNER조건인 지역등록(OWNER필요) 시도 후 실패")
    void testCreateRegionFail() throws Exception {

        // 1) "ownerUser" 회원가입 (기본 ROLE_CUSTOMER)
        String customerUsername = "customuser";
        String customerPassword = "UserPass123!";
        UUID ownerUserId = signupUserAndGetId(customerUsername, customerPassword);
        assertThat(ownerUserId).isNotNull();


        // 2) "ownerUser" 로그인 -> 이제 ROLE_OWNER
        String ownerToken = loginAndGetBearerToken(customerUsername, customerPassword);

        // 3) OWNER 권한으로 스토어 생성
        UUID storeId = createTestStore(ownerToken);

        // 4) OWNER 권한으로 Region 생성
        RegionReqDto regionReqDto = new RegionReqDto();
        regionReqDto.setStoreId(storeId);
        regionReqDto.setProvince("도시");
        regionReqDto.setCity("서울");
        regionReqDto.setLocality("중학동");

        mockMvc.perform(post("/api/region")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, ownerToken)
                        .content(objectMapper.writeValueAsString(regionReqDto)))
                .andExpect(status().isForbidden()).
                andExpect(jsonPath("$.msg").value("(가계주인)허가된 사용자가 아닙니다.")); // 응답 메시지 검증;
    }


    // 회원가입 후 응답에서 userId 추출
    private UUID signupUserAndGetId(String username, String password) throws Exception {
        SignupReqDto signupDto = new SignupReqDto(username, password, "test@test.com", "nick");

        // 회원가입 응답
        String signupResponse = mockMvc.perform(post("/api/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        // 응답 JSON -> userId 추출
        // 예: {"userId":"...","username":"...","email":"...","nickname":"...","role":"ROLE_CUSTOMER"}
        Map<String, Object> responseMap = objectMapper.readValue(signupResponse, new TypeReference<>() {
        });
        String userIdStr = (String) responseMap.get("userId");
        return UUID.fromString(userIdStr);
    }

    // 로그인 후 토큰
    private String loginAndGetBearerToken(String username, String password) throws Exception {
        LoginRequestDto loginDto = new LoginRequestDto(username, password);

        String token = mockMvc.perform(post("/api/user/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getHeader(HttpHeaders.AUTHORIZATION);

        return "Bearer " + token;
    }

    // DB 접근: userId로 엔티티 찾아서 role 변경 (엔티티가 불변이라면 새로 빌더 or 생성자)
    private void forceChangeUserRole(UUID userId, UserRoles newRole) {
        Optional<User> userOpt = userRepository.findByUserIdAndDeletedAtIsNull(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalStateException("User not found in test: " + userId);
        }
        User user = userOpt.get();

        // 빌더 사용 (toBuilder 등)
        User updatedUser = user.toBuilder()
                .role(newRole)
                .build();

        userRepository.save(updatedUser);
    }

    // 스토어 생성
    private UUID createTestStore(String token) throws Exception {
        StoreReqDto storeReqDto = new StoreReqDto();
        storeReqDto.setName("홍콩반점");
        storeReqDto.setAddress("서울시 종로구");
        storeReqDto.setCategory(Category.중식);

        String responseStr = mockMvc.perform(post("/api/store")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .content(objectMapper.writeValueAsString(storeReqDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Map<String, Object> map = objectMapper.readValue(responseStr, new TypeReference<>() {
        });
        String storeIdStr = (String) map.get("storeId");
        return UUID.fromString(storeIdStr);
    }
}
