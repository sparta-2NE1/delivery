package com.sparta.delivery.domain.token.service;

import com.sparta.delivery.domain.token.interfaces.JwtService;
import com.sparta.delivery.domain.user.entity.User;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtServiceImpl implements JwtService {

    private final JwtUtil jwtUtil; // JWT 관련 유틸리티 클래스

    private final Long accessExpiredMs; // Access Token 만료 시간 (밀리초 단위)
    private final Long refreshExpiredMs; // Refresh Token 만료 시간 (밀리초 단위)


    /**
     * JwtServiceImpl 생성자
     *
     * @param jwtUtil JwtUtil 객체 (JWT 토큰 생성 및 검증)
     * @param accessExpiredMs Access Token의 만료 시간 (애플리케이션 설정 값)
     * @param refreshExpiredMs Refresh Token의 만료 시간 (애플리케이션 설정 값)
     */
    public JwtServiceImpl(JwtUtil jwtUtil,
                          @Value("${spring.jwt.accessTokenValidityInMilliseconds}") Long accessExpiredMs,
                          @Value("${spring.jwt.refreshTokenValidityInMilliseconds}") Long refreshExpiredMs) {
        this.jwtUtil = jwtUtil;
        this.accessExpiredMs = accessExpiredMs;
        this.refreshExpiredMs = refreshExpiredMs;
    }

    /**
     * User 정보를 기반으로 Access Token을 생성하는 메서드
     *
     * @param user 사용자 정보
     * @return 생성된 Access Token
     */
    @Override
    public String createAccessToken(User user) {
        return jwtUtil.createJwt("access",user.getUsername(), user.getEmail(), user.getRole(),accessExpiredMs);
    }

    /**
     * User 정보를 기반으로 Refresh Token을 생성하는 메서드
     *
     * @param user 사용자 정보
     * @return 생성된 Refresh Token
     */
    @Override
    public String createRefreshToken(User user) {
        return jwtUtil.createJwt("refresh",user.getUsername(), user.getEmail(), user.getRole(),refreshExpiredMs);
    }

    /**
     * JWT 토큰이 만료되었는지 확인하는 메서드
     *
     * @param token JWT 토큰
     * @return 토큰이 만료되었으면 true, 그렇지 않으면 false
     */
    @Override
    public boolean isTokenExpired(String token) {

        try{
            jwtUtil.isExpired(token);
            return false;
        }catch (ExpiredJwtException e){
            return true;
        }
    }

    /**
     * User 정보를 기반으로 category 값을 추출하는 메서드
     *
     * @param token JWT 토큰
     * @return 토큰에 포함된 category 값
     */
    @Override
    public String getCategory(String token) {
        return jwtUtil.getCategory(token);
    }
}
