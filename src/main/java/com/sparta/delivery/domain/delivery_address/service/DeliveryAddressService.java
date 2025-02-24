package com.sparta.delivery.domain.delivery_address.service;

import com.querydsl.core.BooleanBuilder;
import com.sparta.delivery.config.auth.PrincipalDetails;
import com.sparta.delivery.config.global.exception.custom.DeliveryAddressNotFoundException;
import com.sparta.delivery.config.global.exception.custom.ForbiddenException;
import com.sparta.delivery.config.global.exception.custom.UserNotFoundException;
import com.sparta.delivery.domain.delivery_address.dto.AddressReqDto;
import com.sparta.delivery.domain.delivery_address.dto.AddressResDto;
import com.sparta.delivery.domain.delivery_address.dto.AddressSearchDto;
import com.sparta.delivery.domain.delivery_address.entity.DeliveryAddress;
import com.sparta.delivery.domain.delivery_address.entity.QDeliveryAddress;
import com.sparta.delivery.domain.delivery_address.repository.DeliveryAddressRepository;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryAddressService {

    private final DeliveryAddressRepository addressRepository; // 배송지를 관리하는 repository
    private final UserRepository userRepository; // User를 관리하는 repository

    /**
     * 유저의 배송지를 추가하는 기능
     *
     * 배송지 추가 요청(addressReqDto)을 받아 다음 절차를 수행합니다:
     * 1. 해당 ID의 사용자가 존재하며 논리적으로 삭제되지 않았는지 확인
     *    - 존재하지 않으면 UserNotFoundException을 발생시킵니다.
     * 2. 해당 user가 가지고있는 논리적 삭제되지 않은 배송지 중에 동일한 배송지명이 존재하는지 검사
     *    - 동일한 배송지가 이미 존재할 경우 IllegalArgumentException을 발생시킵니다.
     * 3. 해당 user가 가지고있는 논리적 삭제되지 않은 배송지의 수가 3개 이상인지 검사
     *    - 배송지가 3개 이상일 경우 IllegalArgumentException을 발생시킵니다.
     * 4. 새로운 배송지를 생성하고 유저와의 관계 설정
     * 5. DB에 저장 후 생성된 배송지 정보를 반환
     *
     * @param addressReqDto 배송지 추가에 필요한 정보를 담고 있는 DTO (배송지 이름, 배송지 정보 등)
     * @param principalDetails 현재 인증된 사용자 정보 (로그인한 사용자 정보)
     * @return 생성된 배송지 정보를 담은 AddressResDto 객체
     * @throws UserNotFoundException 해당 id 의 유저가 존재하지 않거나 논리적 삭제 되었을 경우
     * @throws IllegalArgumentException 동일한 배송지명이 존재하거나 배송지 수가 3개 이상인 경우 발생
     */
    public AddressResDto addAddress(AddressReqDto addressReqDto, PrincipalDetails principalDetails) {

        User user = userRepository.findByUsernameAndDeletedAtIsNull(principalDetails.getUsername())
                .orElseThrow(()-> new UserNotFoundException("Invalid username : " + principalDetails.getUsername()));

        // 동일한 user를 가지고있는 deliveryAddress 중에 중복된 명이 있으면 중복 반환
        // 삭제된 deliveryAddress 는 중복 검사에서 제외
        if(addressRepository.existsByUserAndDeliveryAddressAndDeletedAtIsNull(user, addressReqDto.getDeliveryAddress())){
            throw new IllegalArgumentException("해당 유저의 동일한 배송지가 이미 존재합니다. :" + addressReqDto.getDeliveryAddress());
        }

        // user는 가지고있는 deliveryAddress수가 3개 까지만 가질 수 있음
        // 삭제된 deliveryAddress 는 count 에서 제외
        if (addressRepository.countByUserAndDeletedAtIsNull(user) >= 3){
            throw new IllegalArgumentException("배송지는 최대 3개 까지만 추가할 수 있습니다.");
        }

        DeliveryAddress deliveryAddress = DeliveryAddress.builder()
                .deliveryAddress(addressReqDto.getDeliveryAddress())
                .deliveryAddressInfo(addressReqDto.getDeliveryAddressInfo())
                .detailAddress(addressReqDto.getDetailAddress() != null ? addressReqDto.getDetailAddress() : "")
                .user(user)
                .build();

        user.addDeliveryAddress(deliveryAddress);

        userRepository.save(user);

        return deliveryAddress.toResponse();
    }

    /**
     * 배송지 단일 조회 기능
     *
     * 배송지 단일 조회 요청(id)을 받아 다음 절차를 수행합니다:
     * 1. id를 기준으로 논리적 삭제되지 않은 배송지를 찾는다.
     *    - 해당 배송지가 논리적으로 삭제되었거나 존재하지 않으면 DeliveryAddressNotFoundException을 발생시킵니다.
     * 2. 존재하면 배송지 정보를 조회하여 AddressResDto로 변환하여 반환합니다.
     *
     * @param id 조회할 배송지의 ID
     * @return 배송지 정보를 담은 AddressResDto 객체
     * @throws DeliveryAddressNotFoundException 해당 id 의 배송지가 존재하지 않거나 논리적으로 삭제 되었을 경우
     */
    @Transactional(readOnly = true)
    public AddressResDto getDeliveryAddressById(UUID id) {

        DeliveryAddress deliveryAddress = addressRepository.findByDeliveryAddressIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new DeliveryAddressNotFoundException("DeliveryAddress Not Found By Id : "+ id));

        return  deliveryAddress.toResponse();
    }

    /**
     * 배송지 목록 조회 기능
     *
     * 검색 조건을 기반으로 논리적으로 삭제되지 않은 배송지 목록을 조회하는 기능입니다.
     * 1. 검색 조건을 기반으로 배송지 목록을 조회 (검색 조건: deliveryAddress, deliveryAddressInfo 등)
     * 2. 페이지네이션을 적용하여 일정 개수만 조회합니다.
     * 3. 조회된 배송지가 없을 경우 DeliveryAddressNotFoundException을 발생시킵니다.
     * 4. 조회된 배송지 목록을 AddressResDto 형태로 변환하여 반환합니다.
     *
     * @param addressSearchDto 검색 조건 및 페이지 정보를 담고 있는 DTO
     * @return 조회된 배송지 목록을 담은 Page<AddressResDto> 객체
     * @throws DeliveryAddressNotFoundException 해당 조건에 맞는 배송지가 없을 경우 발생
     */
    @Transactional(readOnly = true)
    public Page<AddressResDto> getDeliveryAddresses(AddressSearchDto addressSearchDto) {

        QDeliveryAddress qDeliveryAddress = QDeliveryAddress.deliveryAddress1;

        BooleanBuilder builder = buildSearchConditions(addressSearchDto,qDeliveryAddress);

        // 페이지네이션 설정
        Sort sort = getSortOrder(addressSearchDto);

        PageRequest pageRequest = PageRequest.of(addressSearchDto.getPage(), addressSearchDto.getSize(),sort);

        // 배송지 목록 조회 (페이징 + 검색 조건)
        Page<DeliveryAddress> deliveryAddressPages = addressRepository.findAll(builder,pageRequest);

        if (deliveryAddressPages.isEmpty()){
            throw new DeliveryAddressNotFoundException("deliveryAddress Not Found");
        }

        return deliveryAddressPages.map(DeliveryAddress :: toResponse);
    }

    /**
     * 배송지 정보 수정 기능
     *
     * 주어진 배송지 ID(id)와 수정 요청 정보(addressReqDto)를 받아 다음 절차를 수행합니다:
     * 1. 해당 ID의 배송지가 존재하며 논리적으로 삭제되지 않았는지 확인
     *    - 존재하지 않으면 DeliveryAddressNotFoundException을 발생
     * 2. 현재 로그인한 사용자가 본인인지, 또는 관리자(ROLE_MASTER)인지 검증
     *    - 본인이 아니고, 관리자 권한도 없을 경우 ForbiddenException을 발생
     * 3. 사용자가 수정하려는 배송지의 정보가 본인 소유인지 확인
     *    - 본인이 아니면 접근이 거부
     * 4. 수정된 배송지 정보를 저장
     * 5. 수정된 배송지 정보를 AddressResDto 형태로 반환
     *
     * @param id 배송지 ID (수정할 배송지의 고유 ID)
     * @param addressReqDto 배송지 수정에 필요한 정보 (새로운 주소, 주소 정보, 상세 주소 등을 담고 있는 DTO)
     * @param principalDetails 현재 인증된 사용자 정보 (로그인한 사용자 정보)
     * @return 수정된 배송지 정보를 포함하는 AddressResDto 객체
     * @throws DeliveryAddressNotFoundException 해당 ID의 배송지가 존재하지 않거나 삭제된 경우 발생
     * @throws ForbiddenException 본인이 아니거나 관리자 권한이 없을 경우 발생
     */
    public AddressResDto updateDeliveryAddresses(UUID id, AddressReqDto addressReqDto, PrincipalDetails principalDetails) {

        DeliveryAddress deliveryAddress = addressRepository.findByDeliveryAddressIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new DeliveryAddressNotFoundException("DeliveryAddress Not Found By Id : "+ id));

        if (!deliveryAddress.getUser().getUsername().equals(principalDetails.getUsername()) &&
                !principalDetails.getRole().name().equals("ROLE_MASTER")){
            throw new ForbiddenException("Access denied.");
        }

        DeliveryAddress updateDeliveryAddress = deliveryAddress.toBuilder()
                .deliveryAddress(addressReqDto.getDeliveryAddress())
                .deliveryAddressInfo(addressReqDto.getDeliveryAddressInfo())
                .detailAddress(addressReqDto.getDetailAddress())
                .build();

        return addressRepository.save(updateDeliveryAddress).toResponse();
    }

    /**
     * 배송지 정보 삭제 기능
     *
     * 주어진 배송지 ID(id)를 받아 다음 절차를 수행합니다:
     * 1. 해당 ID의 배송지가 존재하며 논리적으로 삭제되지 않았는지 확인
     *    - 존재하지 않으면 DeliveryAddressNotFoundException을 발생
     * 2. 현재 로그인한 사용자가 본인인지, 또는 관리자(ROLE_MASTER)인지 검증
     *    - 본인이 아니고, 관리자 권한도 없을 경우 ForbiddenException을 발생
     * 3. 논리적 삭제 정보를 업데이트
     *
     * @param id 삭제할 배송지의 고유 ID
     * @param principalDetails 현재 인증된 사용자 정보 (로그인한 사용자 정보)
     * @throws DeliveryAddressNotFoundException 해당 ID의 배송지가 존재하지 않거나 삭제된 경우 발생
     * @throws ForbiddenException 본인이 아니거나 관리자 권한이 없을 경우 발생
     */
    public void deleteDeliveryAddresses(UUID id, PrincipalDetails principalDetails) {

        DeliveryAddress deliveryAddress = addressRepository.findByDeliveryAddressIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new DeliveryAddressNotFoundException("DeliveryAddress Not Found By Id : "+ id));

        if (!deliveryAddress.getUser().getUsername().equals(principalDetails.getUsername()) &&
                !principalDetails.getRole().name().equals("ROLE_MASTER")){
            throw new ForbiddenException("Access denied.");
        }

        deliveryAddress.setDeletedAt(LocalDateTime.now());
        deliveryAddress.setDeletedBy(principalDetails.getUsername());

        addressRepository.save(deliveryAddress);
    }

    /**
     * 사용자 검색 조건을 설정하는 메서드
     */
    private BooleanBuilder buildSearchConditions(AddressSearchDto addressSearchDto, QDeliveryAddress qDeliveryAddress) {

        BooleanBuilder builder = new BooleanBuilder();

        // deliveryAddress 조건
        if (addressSearchDto.getDeliveryAddress() != null && !addressSearchDto.getDeliveryAddress().isEmpty()){
            builder.and(qDeliveryAddress.deliveryAddress.containsIgnoreCase(addressSearchDto.getDeliveryAddress()));
        }

        // deliveryAddressInfo 조건
        if (addressSearchDto.getDeliveryAddressInfo() != null && !addressSearchDto.getDeliveryAddressInfo().isEmpty()){
            builder.and(qDeliveryAddress.deliveryAddressInfo.containsIgnoreCase(addressSearchDto.getDeliveryAddressInfo()));
        }

        builder.and(qDeliveryAddress.deletedAt.isNull());

        return builder;
    }

    /**
     * 정렬 기준을 설정하는 메서드
     */
    private Sort getSortOrder(AddressSearchDto addressSearchDto) {

        String sortBy = addressSearchDto.getSortBy();

        if (!isValidSortBy(sortBy)) {
            throw new IllegalArgumentException("SortBy 는 'createdAt', 'updatedAt', 'deletedAt' 값만 허용합니다.");
        }

        Sort sort = Sort.by(Sort.Order.by(sortBy));

        sort = getSortDirection(sort, addressSearchDto.getOrder());

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
