package com.sparta.delivery.domain.user.service;

import com.sparta.delivery.domain.user.dto.UserReqDto;
import com.sparta.delivery.domain.user.dto.UserResDto;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.enums.UserRoles;
import com.sparta.delivery.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder  passwordEncoder;

    public UserResDto signup(UserReqDto userReqDto) {

        if (userRepository.existsByUsername(userReqDto.getUsername())){
            throw new IllegalArgumentException("username already exists : " +userReqDto.getUsername());
        }

        User user = User.builder()
                .email(userReqDto.getEmail())
                .password(passwordEncoder.encode(userReqDto.getPassword()))
                .username(userReqDto.getUsername())
                .nickname(userReqDto.getNickname())
                .role(UserRoles.ROLE_CUSTOMER)
                .build();
        // 권한은 관리자 검증 후 관리자가 권한을 변경하는 방법으로 하겠습니다.
        // 최초의 MASTER 는 DB에서 직접 설정

        user.setCreatedBy(user.getEmail());

        return userRepository.save(user).toResponseDto();
    }
}
