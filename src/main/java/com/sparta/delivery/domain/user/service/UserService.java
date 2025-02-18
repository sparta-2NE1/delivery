package com.sparta.delivery.domain.user.service;

import com.sparta.delivery.config.auth.PrincipalDetails;
import com.sparta.delivery.config.global.exception.custom.ForbiddenException;
import com.sparta.delivery.config.global.exception.custom.UserNotFoundException;
import com.sparta.delivery.config.jwt.JwtUtil;
import com.sparta.delivery.domain.user.dto.*;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.enums.UserRoles;
import com.sparta.delivery.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
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

        return userRepository.save(user).toResponseDto();
    }

    public JwtResponseDto authenticateUser(LoginRequestDto loginRequestDto) {

        User user = userRepository.findByUsernameAndDeletedAtIsNull(loginRequestDto.getUsername())
                .orElseThrow(()-> new IllegalArgumentException("Invalid username : " + loginRequestDto.getUsername()));

        if (!passwordEncoder.matches(loginRequestDto.getPassword(),user.getPassword() )){
            throw new IllegalArgumentException("Invalid password : " + loginRequestDto.getPassword());
        }

        String accessToken = jwtUtil.createJwt(user.getUsername(), user.getEmail(), user.getRole());

        return new JwtResponseDto(accessToken);
    }

    @Transactional(readOnly = true)
    public UserResDto getUserById(UUID id) {

        User user = userRepository.findByUserIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new UserNotFoundException("User Not Found By Id : " + id));

        return user.toResponseDto();
    }

    @Transactional(readOnly = true)
    public Page<UserResDto> getUsers(Pageable pageable) {
        // Pageable 로받아오면 자동으로 페이지 번호(page) 와 size 가 음수 값으로 들어와도
        // page = 0 , size = 10 으로 기본값으로 처리 해준다.

        Page<User> userPage = userRepository.findAllDeletedIsNull(pageable);

        if (userPage.isEmpty()){
            throw new UserNotFoundException("Users Not Found");
        }

        // Page<User> -> Page<UserResDto>
        return userPage.map(User::toResponseDto);
    }

    public UserResDto updateUser(UUID id, PrincipalDetails principalDetails, UserUpdateReqDto userUpdateReqDto) {
        User user = userRepository.findByUserIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new UserNotFoundException("User Not Found By Id : " + id));

        if (!user.getUsername().equals(principalDetails.getUsername()) && !principalDetails.getRole().name().equals("ROLE_MASTER")){
            throw new ForbiddenException("Access denied.");
        }

        if (!passwordEncoder.matches(userUpdateReqDto.getCurrentPassword(),user.getPassword())){
            throw new ForbiddenException("Incorrect password.");
        }

        User updateUser = user.toBuilder()
                .password(passwordEncoder.encode(userUpdateReqDto.getNewPassword()))
                .email(userUpdateReqDto.getEmail())
                .nickname(userUpdateReqDto.getNickname())
                .build();

        return userRepository.save(updateUser).toResponseDto();
    }

    public UserResDto updateRole(UUID id, PrincipalDetails principalDetails, UserRoleUpdateReqDto userRoleUpdateReqDto) {
        if (!principalDetails.getRole().name().equals("ROLE_MASTER")){
            throw new ForbiddenException("Access denied.");
        }

        User user = userRepository.findByUserIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new UserNotFoundException("User Not Found By Id : " + id));

        User updateUser = user.toBuilder()
                .role(userRoleUpdateReqDto.getRole())
                .build();

        return userRepository.save(updateUser).toResponseDto();
    }

    public void deleteUser(UUID id, PrincipalDetails principalDetails) {
        User user = userRepository.findByUserIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new UserNotFoundException("User Not Found By Id : " + id));

        if (!user.getUsername().equals(principalDetails.getUsername()) &&
                !principalDetails.getRole().name().equals("ROLE_MASTER") &&
                !principalDetails.getRole().name().equals("ROLE_MANAGER")){
            throw new ForbiddenException("Access denied.");
        }

        user.setDeletedAt(LocalDateTime.now());
        user.setDeletedBy(principalDetails.getUsername());

        userRepository.save(user);
    }
}
