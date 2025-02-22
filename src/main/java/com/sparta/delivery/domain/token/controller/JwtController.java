package com.sparta.delivery.domain.token.controller;

import com.sparta.delivery.domain.token.service.RefreshTokenServiceImpl;
import com.sparta.delivery.domain.token.swagger.TokenSwaggerDocs;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name ="Token API", description = "토큰 관련 API")
@RestController
@RequestMapping("/api/token")
@RequiredArgsConstructor
public class JwtController {

    private final RefreshTokenServiceImpl refreshTokenService;

    @TokenSwaggerDocs.reissueAccessToken
    @PostMapping("/reissue")
    public ResponseEntity<?> reissueAccessToken(@CookieValue(name = "refresh", defaultValue = "")String refreshToken){

        String newAccessToken = refreshTokenService.reissueAccessToken(refreshToken);

        return ResponseEntity.status(HttpStatus.OK)
                .header("Authorization", newAccessToken)
                .build();
    }

}
