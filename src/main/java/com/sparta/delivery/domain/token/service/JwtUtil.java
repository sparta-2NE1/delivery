package com.sparta.delivery.domain.token.service;

import com.sparta.delivery.domain.user.enums.UserRoles;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;

    /**
     * JwtUtil 생성자
     *
     * @param secretKey Jwt 시크릿 키를 받아 SecretKey 객체를 생성합니다.
     * @throws IllegalArgumentException 제공된 키가 유효하지 않으면 예외가 발생할 수 있습니다.
     */
    public JwtUtil(@Value("${spring.jwt.secret}") String secretKey) {
        this.secretKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());

    }


    /**
     * JWT 토큰에서 username을 추출하는 메서드
     *
     * @param token JWT 토큰
     * @return payload에서 추출한 username
     * @throws IllegalArgumentException 토큰이 잘못된 경우 예외 발생 가능
     */
    public String getUsername(String token){
        Claims claims = parseClaims(token);
        return claims.get("username",String.class);
    }

    /**
     * JWT 토큰에서 role을 추출하는 메서드
     *
     * @param token JWT 토큰
     * @return payload에서 추출한 role
     * @throws IllegalArgumentException 토큰이 잘못된 경우 예외 발생 가능
     */
    public String getRole(String token){
        Claims claims = parseClaims(token);
        return claims.get("role",String.class);
    }

    /**
     * JWT 토큰에서 email을 추출하는 메서드
     *
     * @param token JWT 토큰
     * @return payload에서 추출한 email
     * @throws IllegalArgumentException 토큰이 잘못된 경우 예외 발생 가능
     */
    public String getEmail(String token){
        Claims claims = parseClaims(token);
        return claims.get("email",String.class);
    }


    /**
     * JWT 토큰에서 category 값을 추출하는 메서드
     *
     * @param token JWT 토큰
     * @return payload에서 추출한 category
     * @throws IllegalArgumentException 토큰이 잘못된 경우 예외 발생 가능
     */
    public String getCategory(String token){
        Claims claims = parseClaims(token);
        return claims.get("category",String.class);
    }

    /**
     * JWT 토큰의 유효시간을 검증하는 메서드
     *
     * @param token JWT 토큰
     * @return 토큰이 만료되었으면 true, 그렇지 않으면 false
     */
    public boolean isExpired(String token) {
        Claims claims = parseClaims(token); // Claims 파싱
        return claims.getExpiration().before(new Date()); // 만료 체크, 만료 시 true
    }

    /**
     * JWT 토큰을 생성하는 메서드
     *
     * @param category 토큰의 카테고리 (예: "access", "refresh" 등)
     * @param username 사용자 이름
     * @param email 사용자 이메일
     * @param role 사용자 역할 (USER, ADMIN 등)
     * @param expiredMs 토큰의 만료시간 (밀리초 단위)
     * @return 생성된 JWT 토큰
     */
    public String createJwt(String category , String username, String email ,UserRoles role, Long expiredMs){
        return Jwts.builder()
                .claim("category",category)
                .claim("username",username)
                .claim("email",email)
                .claim("role",role.name())
                .issuedAt(new Date(System.currentTimeMillis()))
                .issuer("2NE1")
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    /**
     * JWT에서 Claims를 파싱하는 공통 메서드
     *
     * @param token JWT 토큰
     * @return 파싱된 Claims 객체
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
