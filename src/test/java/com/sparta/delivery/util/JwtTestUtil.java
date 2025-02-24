package com.sparta.delivery.util;

import com.sparta.delivery.domain.user.enums.UserRoles;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTestUtil {

    private final SecretKey secretKey;

    public JwtTestUtil(@Value("${spring.jwt.secret}") String secretKey) {
        this.secretKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String createJwt(String username, UserRoles role){
        return Jwts.builder()
                .claim("category", "access")
                .claim("username",username)
                .claim("email", "leeseowoo@test.com")
                .claim("role", role.name())
                .issuedAt(new Date(System.currentTimeMillis()))
                .issuer("2NE1")
                .expiration(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24)))
                .signWith(secretKey)
                .compact();
    }
}
