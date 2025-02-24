package com.sparta.delivery.userTest.integration;

import com.sparta.delivery.config.auth.PrincipalDetails;
import com.sparta.delivery.config.global.exception.custom.ForbiddenException;
import com.sparta.delivery.config.global.exception.custom.UserNotFoundException;
import com.sparta.delivery.domain.user.dto.*;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.enums.UserRoles;
import com.sparta.delivery.domain.user.repository.UserRepository;
import com.sparta.delivery.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles("test") // application-test.yml을 기준으로 test 실행 환경을 구성합니다.
@SpringBootTest  // Spring 컨텍스트를 로드하여 통합 테스트 수행
@Transactional  // 각 테스트 후 롤백하여 데이터 정합성 유지
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private UUID userId;

    @BeforeEach
    void setUp() {

        // 테스트용 사용자 생성 및 저장
        testUser = User.builder()
                .userId(UUID.randomUUID())
                .email("test@example.com")
                .password(passwordEncoder.encode("password"))
                .username("testuser")
                .nickname("testnick")
                .role(UserRoles.ROLE_CUSTOMER)
                .build();

        testUser = userRepository.save(testUser);
        userId = testUser.getUserId();
    }

    @Test
    @DisplayName("회원가입 성공 테스트")
    void testSignupSuccess() {
        // Given
        SignupReqDto signupReqDto = new SignupReqDto("newtestuser", "password", "new@example.com", "newnick");

        // When
        UserResDto result = userService.signup(signupReqDto);

        // Then
        assertNotNull(result);
        assertEquals("new@example.com", result.getEmail());
    }

    @Test
    @DisplayName("회원가입 실패 테스트 - 동일한 username이 존재하는경우 ")
    void testSignupFail(){
        // Given
        SignupReqDto signupReqDto = new SignupReqDto("testuser", "password", "new@example.com", "newnick");

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.signup(signupReqDto);
        });

        // Then
        assertEquals("username already exists : testuser", exception.getMessage());
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void testAuthenticateUserSuccess() {
        // Given
        LoginRequestDto loginRequestDto = new LoginRequestDto("testuser", "password");

        // When
        AuthTokenData response = userService.authenticateUser(loginRequestDto);

        // Then
        assertNotNull(response);
        assertNotNull(response.getAccessToken());
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void testAuthenticateUserFailPassword(){
        // Given
        LoginRequestDto loginRequestDto = new LoginRequestDto("testuser", "1234");

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, ()->{
           userService.authenticateUser(loginRequestDto);
        });

        // Then
        assertEquals("Invalid password : 1234" , exception.getMessage());
    }

    @Test
    @DisplayName("유저 단일 조회 성공")
    void testGetUserByIdSuccess() {
        // When
        UserResDto result = userService.getUserById(userId);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
    }

    @Test
    @DisplayName("유저 단일 조회 실패 - 존재하지않는 ID(UUID)")
    void testGetUserByIdFail(){
        // Given
        UUID failUserId = UUID.randomUUID(); // 임의의 UUID 생성

        // When
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->{
            userService.getUserById(failUserId);
        });

        // Then
        assertEquals("User Not Found By Id : " + failUserId, exception.getMessage());

    }

    @Test
    @DisplayName("모든 유저 조회 성공")
    void testGetUsersSuccess(){

        // Given
        UserSearchReqDto userSearchReqDto = new UserSearchReqDto();
        userSearchReqDto.setPage(0);
        userSearchReqDto.setSize(10);
        userSearchReqDto.setSortBy("createdAt");
        userSearchReqDto.setOrder("asc");

        for(int i = 0; i < 10; i++){
            User testUser = User.builder()
                    .userId(UUID.randomUUID()) // UUID 생성
                    .username("testuser"+ i)
                    .password("password123") // 테스트에서는 암호화 없이 설정
                    .email("testuser@example.com")
                    .nickname("Test User")
                    .role(UserRoles.ROLE_CUSTOMER) // 유저 권한 설정
                    .deliveryAddresses(new ArrayList<>()) // 배송 주소 리스트 초기화
                    .build();
            userRepository.save(testUser);
        }

        Page<UserResDto> result = userService.getUsers(userSearchReqDto);

        assertNotNull(result);
        assertEquals(11, result.getTotalElements());
        assertEquals("testuser0", result.getContent().get(1).getUsername());
    }

    @Test
    @DisplayName("모든 유저 조회 실패 - 결과 없음")
    void testGetUsersFail(){

        UserSearchReqDto userSearchReqDto = new UserSearchReqDto();
        userSearchReqDto.setPage(0);
        userSearchReqDto.setSize(10);
        userSearchReqDto.setSortBy("createdAt");
        userSearchReqDto.setOrder("asc");
        userSearchReqDto.setUsername("dummyUser");

        // When
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->{
            userService.getUsers(userSearchReqDto);
        });


        // Then
        assertEquals("Users Not Found" , exception.getMessage());

    }


    @Test
    @DisplayName("회원정보 수정 성공")
    void testUpdateUserSuccess() {
        // Given
        UserUpdateReqDto updateReqDto = new UserUpdateReqDto("password", "newPassword", "new@example.com", "newNick");

        PrincipalDetails principalDetails = mock(PrincipalDetails.class);

        when(principalDetails.getUsername()).thenReturn("testuser");
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_CUSTOMER);

        // When
        UserResDto result = userService.updateUser(userId, principalDetails, updateReqDto);

        // Then
        assertNotNull(result);
        assertEquals("new@example.com", result.getEmail());
    }

    @Test
    @DisplayName("회원정보 수정 실패 - 권한 없음")
    void testUpdateUserFailAccessDenied(){

        // Given
        UserUpdateReqDto updateReqDto = new UserUpdateReqDto("password", "newPassword", "new@example.com", "newNick");

        PrincipalDetails principalDetails = mock(PrincipalDetails.class);

        when(principalDetails.getUsername()).thenReturn("dummyUser");
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_CUSTOMER);

        // When
        ForbiddenException exception = assertThrows(ForbiddenException.class, () ->{
          userService.updateUser(userId,principalDetails,updateReqDto);
        });

        // Then
        assertEquals("Access denied.", exception.getMessage());
    }

    @Test
    @DisplayName("회원 권한 수정 성공")
    void testUpdateRoleSuccess() {
        // Given
        UserRoleUpdateReqDto userRoleUpdateReqDto = new UserRoleUpdateReqDto(UserRoles.ROLE_OWNER);

        PrincipalDetails principalDetails = mock(PrincipalDetails.class);

        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_MASTER);

        // When
        UserResDto result = userService.updateRole(userId,principalDetails,userRoleUpdateReqDto);

        // Then
        assertNotNull(result);
        assertEquals("ROLE_OWNER", result.getRole());
    }

    @Test
    @DisplayName("회원 권한 수정 실패 - 권한 없음")
    void testUpdateRoleFail(){

        // Given
        UserRoleUpdateReqDto userRoleUpdateReqDto = new UserRoleUpdateReqDto(UserRoles.ROLE_OWNER);

        PrincipalDetails principalDetails = mock(PrincipalDetails.class);

        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_CUSTOMER);

        // When
        ForbiddenException exception = assertThrows(ForbiddenException.class, () ->{
            userService.updateRole(userId,principalDetails,userRoleUpdateReqDto);
        });

         //Then
        assertEquals("Access denied.", exception.getMessage());
    }

    @Test
    @DisplayName("User 논리적 삭제 성공 - 본인 계정")
    void testDeleteUserSuccess() {

        // Given
        PrincipalDetails principalDetails = mock(PrincipalDetails.class);

        when(principalDetails.getUsername()).thenReturn("testuser");
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_CUSTOMER);

        // When
        userService.deleteUser(userId, principalDetails);

        // Then
        Optional<User> deletedUser = userRepository.findByUserIdAndDeletedAtIsNull(userId);
        assertTrue(deletedUser.isEmpty());
    }

    @Test
    @DisplayName("회원 권한 수정 실패 - 권한 없음")
    void testDeleteUserFailForbidden(){

        // Given
        PrincipalDetails principalDetails = mock(PrincipalDetails.class);

        when(principalDetails.getUsername()).thenReturn("dummyUser");
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_CUSTOMER);

        // When
        ForbiddenException exception = assertThrows(ForbiddenException.class, () ->{
            userService.deleteUser(userId,principalDetails);
        });

        //Then
        assertEquals("Access denied.", exception.getMessage());
    }
}
