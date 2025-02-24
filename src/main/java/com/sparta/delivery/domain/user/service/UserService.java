package com.sparta.delivery.domain.user.service;

import com.querydsl.core.BooleanBuilder;
import com.sparta.delivery.config.auth.PrincipalDetails;
import com.sparta.delivery.config.global.exception.custom.ForbiddenException;
import com.sparta.delivery.config.global.exception.custom.InvalidRefreshTokenException;
import com.sparta.delivery.config.global.exception.custom.UserNotFoundException;
import com.sparta.delivery.domain.token.service.JwtServiceImpl;
import com.sparta.delivery.domain.token.service.RefreshTokenServiceImpl;
import com.sparta.delivery.domain.user.dto.*;
import com.sparta.delivery.domain.user.entity.QUser;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.enums.UserRoles;
import com.sparta.delivery.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    private final JwtServiceImpl jwtService;
    private final RefreshTokenServiceImpl refreshTokenService;


    /**
     * 회원가입 기능
     *
     * 회원가입 요청(signupReqDto)을 받아 다음 절차를 수행합니다:
     * 1. 사용자가 입력한 username 이 이미 존재하는지 확인
     *    - 해당 username을 가진 user가 존재하면 예외 발생
     * 2. 존재하지 않으면 새로운 회원을 생성하여 비밀번호를 암호화하고 저장
     * 3. 저장된 회원 정보를 UserResDto로 반환
     *
     * @param signupReqDto 회원가입에 필요한 username, password, email, nickname 정보를 담고있는 DTO
     * @return 등록된 회원 정보를 담은 UserResDto 객체
     * @throws IllegalArgumentException 이미 존재하는 username인 경우 예외 발생
     */
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

        return userRepository.save(user).toResponseDto();
    }

    /**
     * 로그인 기능
     *
     * 로그인 요청(LoginRequestDto)을 받아 다음 절차를 수행합니다:
     * 1. 입력된 username을 기준으로 논리적 삭제가 되지 않은 유저를 조회
     *    - 해당 유저가 존재하지 않으면 예외 발생
     * 2. 조회된 유저의 비밀번호와 입력된 비밀번호를 비교
     *    - 비밀번호가 일치하지 않으면 예외 발생
     * 3. 유저 정보가 유효하면 JWT 토큰(Access Token, Refresh Token)을 생성
     * 4. 생성된 Refresh Token을 저장하고, 두 개의 토큰을 반환
     *
     * @param loginRequestDto 로그인 요청 정보를 담고 있는 DTO (username, password)
     * @return 발급된 JWT 액세스 토큰 및 리프레시 토큰을 담은 AuthTokenData 객체
     * @throws IllegalArgumentException 존재하지않는 username이거나, 비밀번호가 일치하지 않을 경우 예외 발생
     */
    public AuthTokenData authenticateUser(LoginRequestDto loginRequestDto) {

        User user = userRepository.findByUsernameAndDeletedAtIsNull(loginRequestDto.getUsername())
                .orElseThrow(()-> new UserNotFoundException("Invalid username : " + loginRequestDto.getUsername()));

        if (!passwordEncoder.matches(loginRequestDto.getPassword(),user.getPassword() )){
            throw new IllegalArgumentException("Invalid password : " + loginRequestDto.getPassword());
        }

        String accessToken = jwtService.createAccessToken(user);
        String refreshToken = jwtService.createRefreshToken(user);

        refreshTokenService.addRefreshTokenEntity(user,refreshToken);

        return new AuthTokenData(accessToken,refreshToken);
    }

    /**
     * 로그아웃 기능
     *
     * 로그아웃 요청(refreshToken)을 받아 다음 절차를 수행합니다:
     * 1. refreshToken 의 유효시간이 지났는지 검사
     *    - 해당 토큰의 유효시간이 지났을 경우 예외 발생
     * 2. refreshToken 의 paylod 의 category 값이 `refresh` 인지 검사
     *    - category 의 값이 `refresh` 가 아닌 경우 예외 발생
     * 3. 해당 토큰이 DB에 존재하지는 검사
     *    - 존재하지 않을 시 예외 발생
     * 4. 해당 refreshToken 을 DB에서 제거
     * 5. refresh 쿠키 제거
     *
     * @param refreshToken 쿠키에서 념겨받은 refreshToken
     * @throws InvalidRefreshTokenException refreshToken 이 아니거나 DB에 존재하지 않는 토큰인 경우 예외 발생
     */
    public void removeRefreshToken(String refreshToken) {

        if (jwtService.isTokenExpired(refreshToken)){
            return;
        }

        if (!jwtService.getCategory(refreshToken).equals("refresh")){
            throw new InvalidRefreshTokenException("잘못된 토큰이 들어왔습니다.");
        }

        refreshTokenService.removeRefreshToken(refreshToken);
    }

    /**
     * 사용자 단일 조회 기능
     *
     * 사용자 단일 조회 요청(refreshToken)을 받아 다음 절차를 수행합니다:
     * 1. id를 기준으로 논리적 삭제 하지않은 user를 찾는다.
     *    - id를 기준으로 논리적으로 삭제되지 않은 user를 찾는다.
     * 2. 존재 할시 User 의 객체를 받아와 UserResDto로 변환하여 반환
     *
     * @param id 단일 조회할 유저의 ID
     * @return 조회할 회원 정보를 담은 UserResDto 객체
     * @throws UserNotFoundException 해당 id 의 유저 존재하지않거나 논리적 삭제 되었을 경우 예외 발생
     */
    @Transactional(readOnly = true)
    public UserResDto getUserById(UUID id) {

        User user = userRepository.findByUserIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new UserNotFoundException("User Not Found By Id : " + id));

        return user.toResponseDto();
    }


    /**
     * 사용자 목록 조회 기능
     *
     * 검색 요청(userSearchReqDto)을 받아 다음 절차를 수행합니다:
     * 1. 검색 조건을 기반으로 논리적으로 삭제되지 않은 사용자 목록을 조회
     *    - 검색 조건: username, email, role 등의 값이 있을 경우 해당 조건을 적용하여 조회
     * 2. 페이지네이션을 적용하여 일정 개수만 조회
     * 3. 조회된 사용자가 없을 경우 예외를 발생
     * 4. 조회된 사용자 목록을 UserResDto 형태로 변환하여 반환
     *
     * @param userSearchReqDto 검색 조건 및 페이지 정보를 담고 있는 DTO (page, size, 검색 조건 등)
     * @return 조회된 사용자 목록을 포함하는 Page<UserResDto> 객체
     * @throws UserNotFoundException 검색 조건에 해당하는 사용자가 존재하지 않을 경우 예외 발생
     */
    @Transactional(readOnly = true)
    public Page<UserResDto> getUsers(UserSearchReqDto userSearchReqDto) {
        QUser qUser = QUser.user;

        BooleanBuilder builder = buildSearchConditions(userSearchReqDto,qUser);

        // 페이지네이션 설정
        Sort sort = getSortOrder(userSearchReqDto);

        PageRequest pageRequest = PageRequest.of(userSearchReqDto.getPage(), userSearchReqDto.getSize(),sort);

        // 유저 목록 조회 (페이징 + 검색 조건)
        Page<User> userPages = userRepository.findAll(builder,pageRequest);

        if (userPages.isEmpty()){
            throw new UserNotFoundException("Users Not Found");
        }

        return userPages.map(User :: toResponseDto);
    }

    /**
     * 사용자 정보 수정 기능
     *
     * 주어진 사용자 ID(id)와 수정 요청 정보(userUpdateReqDto)를 받아 다음 절차를 수행합니다:
     * 1. 해당 ID의 사용자가 존재하며 논리적으로 삭제되지 않았는지 확인
     *    - 존재하지 않을 경우 UserNotFoundException을 발생
     * 2. 현재 로그인한 사용자가 본인인지 또는 관리자(ROLE_MASTER)인지 검증
     *    - 본인이 아니고, 관리자 권한도 없을 경우 ForbiddenException을 발생
     * 3. 현재 비밀번호(currentPassword)가 저장된 비밀번호와 일치하는지 확인
     *    - 일치하지 않으면 ForbiddenException을 발생
     * 4. 새로운 비밀번호를 암호화하여 저장하고, 이메일과 닉네임을 업데이트합니다.
     * 5. 수정된 사용자 정보를 UserResDto 형태로 변환하여 반환
     *
     * @param id 수정할 사용자의 ID
     * @param principalDetails 현재 인증된 사용자 정보 (로그인한 사용자)
     * @param userUpdateReqDto 수정할 사용자 정보 (새로운 비밀번호, 이메일, 닉네임 등)
     * @return 수정된 사용자 정보를 포함하는 UserResDto 객체
     * @throws UserNotFoundException 해당 ID의 사용자가 존재하지 않거나 삭제된 경우 발생
     * @throws ForbiddenException 본인이 아니거나 관리자 권한이 없을 경우 발생
     */
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

    /**
     * 사용자 정보 수정 기능
     *
     * 주어진 사용자 ID(id)와 수정 요청 정보(userRoleUpdateReqDto)를 받아 다음 절차를 수행합니다:
     * 1. 현재 로그인한 사용자가 관리자(ROLE_MASTER)인지 검증
     *    - 권리자 권한이 없을 경우 ForbiddenException을 발생
     * 2. 해당 ID의 사용자가 존재하며 논리적으로 삭제되지 않았는지 확인
     *    - 존재하지 않을 경우 UserNotFoundException을 발생
     * 4. 새로운 권한을 업데이트합니다.
     * 5. 수정된 사용자 정보를 UserResDto 형태로 변환하여 반환합니다.
     *
     * @param id 수정할 사용자의 ID
     * @param principalDetails 현재 인증된 사용자 정보 (로그인한 사용자)
     * @param userRoleUpdateReqDto 수정할 사용자 권한 정보 (role)
     * @return 수정된 사용자 정보를 포함하는 UserResDto 객체
     * @throws ForbiddenException 관리자 권한이 없을 경우 발생
     */
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

    /**
     * 사용자 논리적 삭제 기능
     *
     * 주어진 사용자 ID(id)를 받아 다음 절차를 수행합니다:
     * 1. 해당 ID의 사용자가 존재하며 논리적으로 삭제되지 않았는지 확인
     *    - 존재하지 않을 경우 UserNotFoundException을 발생
     * 2. 현재 로그인한 사용자가 본인인지 또는 매니저(ROLE_MANAGER)이거나 최고 관리자(ROLE_MASTER)인지 검증
     *    - 본인이 아니고, 관리자 권한도 없을 경우 ForbiddenException을 발생
     * 3. 논리적 삭제 정보를 업데이트합니다.
     *
     * @param id 수정할 사용자의 ID
     * @param principalDetails 현재 인증된 사용자 정보 (로그인한 사용자)
     * @throws UserNotFoundException 해당 ID의 사용자가 존재하지 않거나 삭제된 경우 발생
     * @throws ForbiddenException 관리자 권한이 없을 경우 발생
     */
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

    /**
     * 사용자 검색 조건을 설정하는 메서드
     */
    private BooleanBuilder buildSearchConditions(UserSearchReqDto userSearchReqDto, QUser qUser) {
        BooleanBuilder builder = new BooleanBuilder();

        // username 조건
        if (userSearchReqDto.getUsername() != null && !userSearchReqDto.getUsername().isEmpty()){
            builder.and(qUser.username.containsIgnoreCase(userSearchReqDto.getUsername()));
        }

        // email 조건
        if (userSearchReqDto.getEmail() != null && !userSearchReqDto.getEmail().isEmpty()){
            builder.and(qUser.email.containsIgnoreCase(userSearchReqDto.getEmail()));
        }

        // role 조건
        if (userSearchReqDto.getRole() != null){
            builder.and(qUser.role.eq(userSearchReqDto.getRole()));
        }

        builder.and(qUser.deletedAt.isNull());

        return builder;
    }

    /**
     * 정렬 기준을 설정하는 메서드
     */
    private Sort getSortOrder(UserSearchReqDto userSearchReqDto) {
        String sortBy = userSearchReqDto.getSortBy();

        if (!isValidSortBy(sortBy)) {
            throw new IllegalArgumentException("SortBy 는 'createdAt', 'updatedAt', 'deletedAt' 값만 허용합니다.");
        }

        Sort sort = Sort.by(Sort.Order.by(sortBy));

        sort = getSortDirection(sort, userSearchReqDto.getOrder());

        return sort;
    }

    /**
     * 정렬 기준 유효성 검사
     */
    private boolean isValidSortBy(String sortBy) {
        return "createdAt".equals(sortBy) || "updatedAt".equals(sortBy) || "deletedAt".equals(sortBy);
    }

    /**
     * 정렬 방향 설정
     */
    private Sort getSortDirection(Sort sort, String order) {
        return  "desc".equals(order) ? sort.descending() : sort.ascending();
    }


}
