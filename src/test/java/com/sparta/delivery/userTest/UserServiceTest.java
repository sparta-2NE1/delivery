package com.sparta.delivery.userTest;


import com.sparta.delivery.config.global.exception.custom.UserNotFoundException;
import com.sparta.delivery.config.jwt.JwtUtil;
import com.sparta.delivery.domain.user.dto.*;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.enums.UserRoles;
import com.sparta.delivery.domain.user.repository.UserRepository;
import com.sparta.delivery.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    private User testUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 테스트용 사용자 생성
        userId = UUID.randomUUID();
        testUser = User.builder()
                .userId(userId)
                .email("test@example.com")
                .password("encodedPassword")
                .username("testuser")
                .nickname("testnick")
                .role(UserRoles.ROLE_CUSTOMER)
                .build();
    }

    @Test
    @DisplayName("회원가입 성공 테스트")
    void testSignupSuccess() {
        // Given
        SignupReqDto signupReqDto = new SignupReqDto("test@example.com", "testuser", "password", "testnick");

        when(userRepository.existsByUsername("testuser")).thenReturn(false); // 해당 username 존재 유무 확인
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword"); // 비밀번호 엄호화
        when(userRepository.save(any(User.class))).thenReturn(testUser); // user entity 에 저장

        // When
        UserResDto result = userService.signup(signupReqDto);

        // Then
        assertNotNull(result); // result 가 null 인지 검사
        assertEquals("test@example.com", result.getEmail()); // null 이 아닌경우 예상한 값과 email이 동일한지 검사
        assertEquals("testuser", result.getUsername()); // null 아닌 경우 예상한 값과 username이 동일한지 검사
    }

    @Test
    @DisplayName("회원가입 실패 테스트")
    void testSignupFail(){
        // Given
        SignupReqDto signupReqDto = new SignupReqDto("test@example.com", "testuser", "password", "testnick");


        // any() 권장
        when(userRepository.existsByUsername(any())).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.signup(signupReqDto));
        assertEquals("username already exists : test@example.com", exception.getMessage());
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void testAuthenticateUserSuccess() {
        // Given
        LoginRequestDto loginRequestDto = new LoginRequestDto("testuser", "password");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password", testUser.getPassword())).thenReturn(true);
        when(jwtUtil.createJwt(anyString(), anyString(), any(UserRoles.class))).thenReturn("accessToken");

        // When
        JwtResponseDto response = userService.authenticateUser(loginRequestDto);

        // Then
        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void testAuthenticateUserFailPassword() {
        // Given
        LoginRequestDto loginRequestDto = new LoginRequestDto("testuser", "1234");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("1234", testUser.getPassword())).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.authenticateUser(loginRequestDto));
        assertEquals("Invalid password : wrongpassword", exception.getMessage());
    }

    @Test
    @DisplayName("유저 단일 조회 성공")
    void testGetUserByIdSuccess() {
        // Given
        when(userRepository.findByUserIdAndDeletedAtIsNull(userId)).thenReturn(Optional.of(testUser));

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
        when(userRepository.findByUserIdAndDeletedAtIsNull(userId)).thenReturn(Optional.empty());


        // When Then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
        assertEquals("User Not Found By Id : " + userId, exception.getMessage());
    }

    @Test
    @DisplayName("모든유저 조회 성공")
    void testGetUsersSuccess(){

        // Given
        PageRequest pageable = PageRequest.of(0,10);
        Page<User> userPage = new PageImpl<>(List.of(testUser));

        when(userRepository.findAllDeletedIsNull(pageable)).thenReturn(userPage);

        // When
        Page<UserResDto> result = userService.getUsers(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

    }

    @Test
    @DisplayName("모든 유저 조회 실패 - 결과 없음")
    void testGetUsersFail(){
        // Given
        PageRequest pageable = PageRequest.of(0,10);
        Page<User> emptyPage = Page.empty();

        when(userRepository.findAllDeletedIsNull(pageable)).thenReturn(emptyPage);

        // When & Then
        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class , ()-> userService.getUsers(pageable));
        assertEquals("Users Not Found", userNotFoundException.getMessage());
    }

}
