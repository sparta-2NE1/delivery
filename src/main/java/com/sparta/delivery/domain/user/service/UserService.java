package com.sparta.delivery.domain.user.service;

import com.sparta.delivery.config.global.exception.custom.UserNotFoundException;
import com.sparta.delivery.config.jwt.JwtUtil;
import com.sparta.delivery.domain.user.dto.JwtResponseDto;
import com.sparta.delivery.domain.user.dto.LoginRequestDto;
import com.sparta.delivery.domain.user.dto.SignupReqDto;
import com.sparta.delivery.domain.user.dto.UserResDto;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.enums.UserRoles;
import com.sparta.delivery.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder  passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserResDto signup(SignupReqDto signupReqDto) {

        if (userRepository.existsByUsername(signupReqDto.getUsername())){
            throw new IllegalArgumentException("username already exists : " + signupReqDto.getUsername());
        }

        User user = User.builder()
                .email(signupReqDto.getEmail())
                .password(passwordEncoder.encode(signupReqDto.getPassword()))
                .username(signupReqDto.getUsername())
                .nickname(signupReqDto.getNickname())
                .role(UserRoles.ROLE_CUSTOMER)
                .build();
        // 권한은 관리자 검증 후 관리자가 권한을 변경하는 방법으로 하겠습니다.
        // 최초의 MASTER 는 DB에서 직접 설정

        user.setCreatedBy(user.getEmail());

        return userRepository.save(user).toResponseDto();
    }

    public JwtResponseDto authenticateUser(LoginRequestDto loginRequestDto) {

        User user = userRepository.findByUsername(loginRequestDto.getUsername())
                .orElseThrow(()-> new IllegalArgumentException("Invalid username : " + loginRequestDto.getUsername()));

        if (!passwordEncoder.matches(loginRequestDto.getPassword(),user.getPassword() )){
            throw new IllegalArgumentException("Invalid password : " + loginRequestDto.getPassword());
        }

        String accessToken = jwtUtil.createJwt(user.getUsername(), user.getEmail(), user.getRole());

        return new JwtResponseDto(accessToken);
    }

    public UserResDto getUserDetail(UUID id) {

        User user = userRepository.findByUserIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new UserNotFoundException("User Not Found By Id : " + id));

        return user.toResponseDto();

    }
}
