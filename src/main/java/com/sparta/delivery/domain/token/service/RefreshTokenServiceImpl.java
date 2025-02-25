package com.sparta.delivery.domain.token.service;

import com.sparta.delivery.config.global.exception.custom.InvalidRefreshTokenException;
import com.sparta.delivery.config.global.exception.custom.RefreshTokenAlreadyExistsException;
import com.sparta.delivery.domain.token.entity.RefreshToken;
import com.sparta.delivery.domain.token.interfaces.RefreshTokenService;
import com.sparta.delivery.domain.token.repository.RefreshTokenRepository;
import com.sparta.delivery.domain.user.entity.User;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository; // 리프레쉬 토큰을 관리하는 repository

    private final JwtUtil jwtUtil; // JWT 관련 유틸리티 클래스
    private final Long accessExpiredMs; // Access Token 만료 시간 (밀리초 단위)
    private final Long refreshExpiredMs; // Refresh Token 만료 시간 (밀리초 단위)

    /**
     * RefreshTokenServiceImpl 생성자
     *
     * @param refreshTokenRepository 리프레쉬 토큰을 데이터베이스에서 관리하는 Repository
     * @param jwtUtil JwtUtil 객체 (JWT 토큰 생성 및 검증)
     * @param accessExpiredMs Access Token의 만료 시간 (애플리케이션 설정 값)
     * @param refreshExpiredMs Refresh Token의 만료 시간 (애플리케이션 설정 값)
     */
    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository,
                                   JwtUtil jwtUtil,
                                   @Value("${spring.jwt.accessTokenValidityInMilliseconds}") Long accessExpiredMs,
                                   @Value("${spring.jwt.refreshTokenValidityInMilliseconds}") Long refreshExpiredMs) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtUtil = jwtUtil;
        this.accessExpiredMs = accessExpiredMs;
        this.refreshExpiredMs = refreshExpiredMs;
    }

    /**
     * Refresh Token을 추가하는 기능
     *
     * 사용자의 Refresh Token을 저장합니다. 다음 절차를 수행합니다.
     * 1. 사용자의 refresh Token이 이미 존재하는지 검사
     *    - 존재하면 해당 토큰이 만료되지 않았는지 검사
     * 2. 만약 만료되지 않은 기존 Refresh Token이 있으면, 이미 로그인된 상태로 간주하고 예외 발생
     * 3. 만약 Refresh Token 존재하지 않거나 만료된 경우, 새로운 Refresh Token을 생성하고 저장
     * @param user 로그인한 User 정보
     * @param refresh DB에 추가할 refresh 토큰
     * @throws RefreshTokenAlreadyExistsException 이미 유효한 Refresh Token이 존재하는 경우 예외 발생
     */
    @Override
    public void addRefreshTokenEntity(User user, String refresh) {

        // 사용자의 RefreshToken 이 이미 존재하는지 확인 (로그인이 되어있는 경우)
        if (refreshTokenRepository.findByUser(user).isPresent()){

            // 기존 RefreshToken이 존재하는 경우, 해당 토큰이 만료되었는지 확인
            if (!jwtUtil.isExpired(refresh)){
                // 토큰이 아직 만료되지 않았다면 이미 로그인된 상태
                throw new RefreshTokenAlreadyExistsException("이미 로그인되었거나 비정상 로그아웃되었습니다.");
            }
        }

        // 기존 RefreshToken이 있지만, 만료된 경우 새로운 RefreshToken 발급
        Date date = new Date(System.currentTimeMillis() + refreshExpiredMs);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .refresh(refresh)
                .expiration(date.toString())
                .build();

        refreshTokenRepository.save(refreshToken);
    }

    /**
     * Refresh Token을 제거하는 기능
     *
     * 사용자의 Refresh Token을 제거합니다. 다음 절차를 수행합니다.
     * 1. 해당 Refresh Token이 DB에 존재하는지 검사
     *    - 존재하지않으면 예외 발생
     * 2. 만약 존재한다면 DB에서 제거
     * 3. refresh 쿠키 제거
     * @param refreshToken 제거할 대상 refresh 토큰
     * @throws InvalidRefreshTokenException DB 에 해당 Refresh Token이 존재하지않는 경우
     */
    @Override
    public void removeRefreshToken(String refreshToken) {
        if(!refreshTokenRepository.existsByRefresh(refreshToken)){
            throw new InvalidRefreshTokenException("등록된 토큰이 아닙니다.");
        }

        refreshTokenRepository.deleteByRefresh(refreshToken);

        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
    }

    /**
     * Access Token 을 재발급하는 기능
     *
     * 사용자의 Access Token을 재발급합니다. 다음 절차를 수행합니다.
     * 1. 해당 Refresh Token이 만료되었는지 검사
     *    - 만료되었으면 예외 발생
     * 2. 만료되었지않았으면 토큰의 category 가 refresh 인지 검사
     *    - refresh token이 아닌 경우 예외 발생
     * 3. Refresh Token을 기반으로 새로운 Access Token 생성
     * 4. 생성한 Access Token 반환
     * @param refreshToken Access Token 을 발급할 Refresh Token
     * @throws ExpiredJwtException JWT 토큰이 만된 경우
     * @throws InvalidRefreshTokenException 해당 토큰이 refresh 토큰이 아닌 경우 이거나 DB에 해당 토큰이 존재하지않는 경우
     */
    @Override
    public String reissueAccessToken(String refreshToken) {

        if (jwtUtil.isExpired(refreshToken)){
            throw new ExpiredJwtException(null, null, "Refresh token is still valid, no need to reissue access token");
        }

        if (!jwtUtil.getCategory(refreshToken).equals("refresh")){
            throw new InvalidRefreshTokenException("Provided token is not a refresh token");
        }

        RefreshToken token = refreshTokenRepository.findByRefresh(refreshToken)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid or non-existent refresh token"));

        User user = token.getUser();

        return jwtUtil.createJwt("access",user.getUsername(),user.getEmail(),user.getRole(),accessExpiredMs);
    }
}
