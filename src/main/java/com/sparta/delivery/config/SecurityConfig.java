package com.sparta.delivery.config;


import com.sparta.delivery.config.filter.JwtAuthenticationFilter;
import com.sparta.delivery.domain.token.service.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());

        http.addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(authorization -> {

            // 공용 URL (인증 없이 접근 가능)
            authorization.requestMatchers(
                    "/api/user/signup",
                    "/api/user/signin",
                    "/api/token/reissue",
                    "/api/user/logout",
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
            ).permitAll();

            // 주문 관련
            // 특정 가게 주문 조회: OWNER, MANAGER, MASTER
            authorization.requestMatchers(
                    HttpMethod.GET,
                    "/api/order/getStoreOrder/{storeId}"
            ).hasAnyRole("OWNER", "MANAGER", "MASTER");

            // 단일 주문 조회 및 사용자 정보 조회: MANAGER, MASTER
            authorization.requestMatchers(
                    HttpMethod.GET,
                    "/api/order/{orderId}",
                    "/api/user/{id}",
                    "api/user"
            ).hasAnyRole("MANAGER", "MASTER");

            // 리뷰 관련 (수정 및 삭제): CUSTOMER, MANAGER, MASTER
            authorization.requestMatchers(
                    HttpMethod.PATCH,
                    "/api/review/deleteReview/{reviewId}",
                    "/api/review/updateReview/{reviewId}"
            ).hasAnyRole("CUSTOMER", "MANAGER", "MASTER");

            // 가게 관련
            // 가게 등록: MANAGER, MASTER
            authorization.requestMatchers(
                    HttpMethod.POST,
                    "/api/store"
            ).hasAnyRole("MANAGER", "MASTER");

            // 가게 삭제: MANAGER, MASTER
            authorization.requestMatchers(
                    HttpMethod.PATCH,
                    "/api/store/{storeId}/delete"
            ).hasAnyRole("MANAGER", "MASTER");

            // 가게 수정: OWNER, MANAGER, MASTER
            authorization.requestMatchers(
                    HttpMethod.PATCH,
                    "/api/store/{storeId}"
            ).hasAnyRole("OWNER", "MANAGER", "MASTER");

            // 결제 내역 및 사용자 권한 변경: MASTER
            authorization.requestMatchers(
                    HttpMethod.PATCH,
                    "/api/payment/{payment_id}",
                    "/api/user/{id}/role"
            ).hasRole("MASTER");

            // 기타 POST 요청 (주문 상태 업데이트, 제품, AI, 지역 등록): OWNER, MANAGER, MASTER
            authorization.requestMatchers(
                    HttpMethod.POST,
                    "/api/products/stores/{storeId}",
                    "/api/ai",
                    "/api/region"
            ).hasAnyRole("OWNER", "MANAGER", "MASTER");

            // 기타 PATCH 요청 (제품, 주문 상태 업데이트, 지역 수정): OWNER, MANAGER, MASTER
            authorization.requestMatchers(
                    HttpMethod.PATCH,
                    "/api/products/{productId}",
                    "/api/products/{productId}/delete",
                    "/api/order/updateOrderStatus/{orderId}",
                    "/api/region/{regionId}",
                    "/api/region/{regionId}/delete"
            ).hasAnyRole("OWNER", "MANAGER", "MASTER");

            // 그 외 모든 요청은 인증된 사용자만 접근
            authorization.anyRequest().authenticated();
        });

        return http.build();
    }
}
