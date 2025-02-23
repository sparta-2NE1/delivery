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

        http.authorizeHttpRequests(authorizationHttpRequest-> {

            // 공용 URL (인증 없이 접근 가능)
            authorizationHttpRequest
                    .requestMatchers("/api/user/signup", "/api/user/signin", "/api/token/reissue", "/swagger-ui/**", "/v3/api-docs/**")
                    .permitAll();

            // GET 요청은 모두 허용
            authorizationHttpRequest
                    .requestMatchers(HttpMethod.GET, "/api/products/**", "/api/region/**")
                    .permitAll();

            // 결제 내역 삭제는 MASTER만 허용
            authorizationHttpRequest
                    .requestMatchers(HttpMethod.PATCH, "/api/payment/**")
                    .hasRole("MASTER");

            // 가게 등록은 "/api/store" 경로에 대해 POST 요청일 때 MANAGER와 MASTER 권한만 허용
            authorizationHttpRequest
                    .requestMatchers(HttpMethod.POST, "/api/store").hasAnyRole("MANAGER", "MASTER");

            // POST, PUT, PATCH 요청에 대해 OWNER, MANAGER, MASTER 권한만 허용 (여러 엔드포인트 그룹화)
            authorizationHttpRequest
                    .requestMatchers(HttpMethod.POST, "/api/store/**", "/api/products/**", "/api/ai", "/api/region/**")
                    .hasAnyRole("OWNER", "MANAGER", "MASTER")
                    .requestMatchers(HttpMethod.PUT, "/api/products/**", "/api/region/**")
                    .hasAnyRole("OWNER", "MANAGER", "MASTER")
                    .requestMatchers(HttpMethod.PATCH, "/api/products/**")
                    .hasAnyRole("OWNER", "MANAGER", "MASTER");

            // 그 외 요청은 인증된 사용자만 접근
            authorizationHttpRequest.anyRequest().authenticated();
                });

        return http.build();
    }
}
